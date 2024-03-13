package com.teenspirit.coderunnerhub.service;


import com.teenspirit.coderunnerhub.dto.*;
import com.teenspirit.coderunnerhub.exceptions.BadRequestException;
import com.teenspirit.coderunnerhub.exceptions.InternalServerErrorException;
import com.teenspirit.coderunnerhub.exceptions.NotFoundException;
import com.teenspirit.coderunnerhub.model.CodeRequest;
import com.teenspirit.coderunnerhub.model.ExecutionResult;
import com.teenspirit.coderunnerhub.model.Problem;
import com.teenspirit.coderunnerhub.model.postgres.StudentAppointment;
import com.teenspirit.coderunnerhub.model.postgres.Test;
import com.teenspirit.coderunnerhub.repository.mongodb.ProblemsRepository;
import com.teenspirit.coderunnerhub.repository.postgres.StudentAppointmentsRepository;
import com.teenspirit.coderunnerhub.repository.postgres.TestsRepository;
import com.teenspirit.coderunnerhub.util.*;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static com.teenspirit.coderunnerhub.util.CAnalyzer.analyzeCCode;

@Service
public class ProblemService {

    @Getter
    private final ProblemsRepository problemRepository;

    private final StudentAppointmentsRepository studentAppointmentsRepository;
    private final TestsRepository testsRepository;
    private final MongoTemplate mongoTemplate;
    private final MessageSender messageSender;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CCodeExecutor cCodeExecutor;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemService.class);

    @Autowired
    public ProblemService(StudentAppointmentsRepository studentAppointmentsRepository,
                          RedisTemplate<String, Object> redisTemplate,
                          ProblemsRepository problemRepository,
                          TestsRepository testsRepository,
                          MongoTemplate mongoTemplate,
                          CCodeExecutor cCodeExecutor,
                          MessageSender messageSender) {
        this.studentAppointmentsRepository = studentAppointmentsRepository;
        this.redisTemplate = redisTemplate;
        this.problemRepository = problemRepository;
        this.testsRepository = testsRepository;
        this.mongoTemplate = mongoTemplate;
        this.cCodeExecutor = cCodeExecutor;
        this.messageSender = messageSender;
    }

    public List<ProblemDTO> getAllProblems() {
        return problemRepository.findAll()
                .stream()
                .map(this::convertProblemToDTO)
                .toList();
    }

    public ProblemDTO getProblemById(int appointmentId) {
        Optional<Problem> optionalProblem = problemRepository.findById(appointmentId);
        if (optionalProblem.isPresent()) {
            return convertProblemToDTO(optionalProblem.get());
        }
        throw new NotFoundException("Problem not found with id: " + appointmentId);
    }

    @Async
    public CompletableFuture<TestRequestDTO> waitForTestResultsAsync(int id) {
        try {
            CompletableFuture<TestRequestDTO> future = new CompletableFuture<>();

            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {
                TestRequestDTO result = (TestRequestDTO) redisTemplate.opsForValue().get("solution:" + id);
                if (result != null) {
                    future.complete(result);
                    executorService.shutdown();
                }
            }, 2, 1, TimeUnit.SECONDS);

            return future;

        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public TestRequestDTO sendTestToQueue(int appointmentId) {
        Optional<Problem> existingProblemOptional = getProblemRepository().findById(appointmentId);
        if (existingProblemOptional.isEmpty()) {
            throw new NotFoundException("Solution with id=" + appointmentId + " not found");
        }
        int hashCode = HashCodeGenerator.getHashCode(getProblemById(appointmentId).getCode());
        TestRequestDTO cachedResult = (TestRequestDTO) redisTemplate.opsForValue().get("solution:" + appointmentId);
        if (cachedResult != null) {
            if (cachedResult.getHashCode() == hashCode) {
                LOGGER.info("Get solution from cache");
                return cachedResult;
            }
        }
        LOGGER.info("Need to create new solution in cache");
        redisTemplate.opsForValue().getOperations().delete("solution:" + appointmentId);
        messageSender.sendMessage(new TestRequestDTO(appointmentId, hashCode));
        CompletableFuture<TestRequestDTO> result = waitForTestResultsAsync(appointmentId);

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerErrorException("Error while testing problem with id " + appointmentId);
        }
    }


    public void processTestRequest(TestRequestDTO testRequestDTO) {
        Optional<Problem> problem = problemRepository.findById(testRequestDTO.getId());
        if (problem.isPresent()) {
            runTests(testRequestDTO, problem.get());
        } else {
            testRequestDTO.setOutput("404, Solution with id " + testRequestDTO.getId() + "not found");
            redisTemplate.opsForValue().set("solution:" + testRequestDTO.getId(), testRequestDTO);
        }
    }

    private void runTests(TestRequestDTO testRequestDTO, Problem problem) {

        Optional<StudentAppointment> appointment = studentAppointmentsRepository.findById(testRequestDTO.getId());

        if (appointment.isEmpty()) {
            testRequestDTO.setOutput("404, Appointment for solution with id " + testRequestDTO.getId() + " not found");
            LOGGER.error("404, Appointment with id " + testRequestDTO.getId() + "not found");
            redisTemplate.opsForValue().set("solution:" + testRequestDTO.getId(), testRequestDTO);

        } else {

            List<Test> testList = testsRepository.findAllByTaskIdAndDeletedFalse(appointment.get().getTaskId());


            int totalTests = testList.size();
            testRequestDTO.setTotalTests(totalTests);

            try {
                File cCode = CCodeGenerator.generateCCode(convertProblemToCodeRequest(problem));
                for (Test test : testList) {
                    String[] inputValues = test.getInput().split(" ");
                    ExecutionResult executionResult = cCodeExecutor.executeCode(cCode, inputValues);
                    if (executionResult.isError()) {
                        testRequestDTO.setOutput("400 " + executionResult.error());
                        LOGGER.error("400 " + executionResult.error());
                        redisTemplate.opsForValue().set("solution:" + testRequestDTO.getId(), testRequestDTO);
                    } else {
                        handleTestResult(executionResult.result(), test, testRequestDTO);
                       // testRequestDTO.setOutput(executionResult.output());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LOGGER.info("Load to cache " + testRequestDTO);
            redisTemplate.opsForValue().set("solution:" + testRequestDTO.getId(), testRequestDTO);
        }
    }

    private void handleTestResult(String result, Test test, TestRequestDTO testRequestDTO) {
        int expectedOutput = Integer.parseInt(test.getOutput());
        int actualOutput = Integer.parseInt(result.trim());

        if (expectedOutput == actualOutput) {
            testRequestDTO.incrementTestPassed();
        }
    }

    public ServiceResult<ProblemDTO> saveProblem(SolutionDTO solutionDTO) throws IOException, InterruptedException {

        String funcName = solutionDTO.getFuncName();
        String code = solutionDTO.getCode();
        String language = solutionDTO.getLanguage();
        int appointmentId = solutionDTO.getAppointmentId();

        if (!isValidLanguage(language)) {
            throw new BadRequestException("Unsupported programming language: " + language);
        }

        Optional<Problem> existingProblemOptional = problemRepository.findById(appointmentId);

        if (existingProblemOptional.isPresent()) {
            Problem existingProblem = existingProblemOptional.get();
            updateProblem(existingProblem, language, code, funcName);

            return new ServiceResult<>(convertProblemToDTO(existingProblem), false);
        } else {
            CAnalyzer.FunctionInfo result = analyzeCCode(code, funcName);

            Problem newProblem = new Problem(appointmentId, language, code, funcName, result.getReturnType(), result.getArguments());
            problemRepository.save(newProblem);

            return new ServiceResult<>(convertProblemToDTO(newProblem), true);
        }
    }

    public String deleteProblemById(int appointmentId) {
        Optional<Problem> existingProblemOptional = getProblemRepository().findById(appointmentId);
        if (existingProblemOptional.isPresent()) {
            problemRepository.deleteById(appointmentId);
            return "Problem with id " + appointmentId + " deleted successfully";
        } else {
            throw new NotFoundException("Problem with id=" + appointmentId + " not found");
        }

    }

    private ProblemDTO convertProblemToDTO(Problem problem) {
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setAppointmentId(problem.getAppointmentId());
        problemDTO.setLanguage(problem.getLanguage());
        problemDTO.setCode(problem.getCode());
        problemDTO.setFunctionName(problem.getFunctionName());
        problemDTO.setReturnType(problem.getReturnType());
        problemDTO.setArguments(problem.getArguments());
        return problemDTO;
    }

    private CodeRequest convertProblemToCodeRequest(Problem problem) {
        return new CodeRequest(problem.getCode(), problem.getFunctionName(), problem.getReturnType(), problem.getArguments());
    }

    private boolean isValidLanguage(String programmingLanguage) {
        return List.of("c", "cpp", "java").contains(programmingLanguage);
    }

    private void updateProblem(Problem existingProblem, String language, String code, String funcName) throws IOException, InterruptedException {
        CAnalyzer.FunctionInfo result = analyzeCCode(code, funcName);
        existingProblem.setLanguage(language);
        existingProblem.setCode(code);
        existingProblem.setFunctionName(funcName);
        existingProblem.setReturnType(result.getReturnType());
        existingProblem.setArguments(result.getArguments());
        System.out.println("RES ARGS: " + result.getArguments());
        Update update = new Update();
        update.set("programmingLanguage", language);
        update.set("code", code);
        update.set("functionName", funcName);
        update.set("returnType", result.getReturnType());
        update.set("arguments", result.getArguments());

        Query query = Query.query(Criteria.where("_id").is(existingProblem.getAppointmentId()));
        mongoTemplate.updateFirst(query, update, Problem.class);
    }

    public List<ProblemDTO> getCodesByAppointmentIds(List<Integer> appointmentIds) {
        List<ProblemDTO> result = new ArrayList<>();
        for (Integer appointmentId : appointmentIds) {
            Optional<Problem> problemOptional = problemRepository.findById(appointmentId);
            if (problemOptional.isPresent()) {
                Problem problem = problemOptional.get();
                ProblemDTO problemDTO = new ProblemDTO(
                        appointmentId,
                        problem.getLanguage(),
                        problem.getCode(),
                        problem.getFunctionName(),
                        problem.getReturnType(),
                        problem.getArguments()
                );
                result.add(problemDTO);
            }
        }
        return result;
    }
}
