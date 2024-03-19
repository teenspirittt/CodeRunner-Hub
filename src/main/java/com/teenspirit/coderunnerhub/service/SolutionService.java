package com.teenspirit.coderunnerhub.service;


import com.teenspirit.coderunnerhub.dto.*;
import com.teenspirit.coderunnerhub.exceptions.BadRequestException;
import com.teenspirit.coderunnerhub.exceptions.InternalServerErrorException;
import com.teenspirit.coderunnerhub.exceptions.NotFoundException;
import com.teenspirit.coderunnerhub.model.CodeRequest;
import com.teenspirit.coderunnerhub.model.ExecutionResult;
import com.teenspirit.coderunnerhub.model.Solution;
import com.teenspirit.coderunnerhub.model.postgres.StudentAppointment;
import com.teenspirit.coderunnerhub.model.postgres.Test;
import com.teenspirit.coderunnerhub.repository.mongodb.SolutionsRepository;
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
public class SolutionService {

    @Getter
    private final SolutionsRepository solutionRepository;

    private final StudentAppointmentsRepository studentAppointmentsRepository;
    private final TestsRepository testsRepository;
    private final MongoTemplate mongoTemplate;
    private final MessageSender messageSender;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CCodeExecutor cCodeExecutor;

    private static final Logger LOGGER = LoggerFactory.getLogger(SolutionService.class);

    @Autowired
    public SolutionService(StudentAppointmentsRepository studentAppointmentsRepository,
                           RedisTemplate<String, Object> redisTemplate,
                           SolutionsRepository solutionRepository,
                           TestsRepository testsRepository,
                           MongoTemplate mongoTemplate,
                           CCodeExecutor cCodeExecutor,
                           MessageSender messageSender) {
        this.studentAppointmentsRepository = studentAppointmentsRepository;
        this.redisTemplate = redisTemplate;
        this.solutionRepository = solutionRepository;
        this.testsRepository = testsRepository;
        this.mongoTemplate = mongoTemplate;
        this.cCodeExecutor = cCodeExecutor;
        this.messageSender = messageSender;
    }

    public List<SolutionDTO> getAllProblems() {
        return solutionRepository.findAll()
                .stream()
                .map(this::convertProblemToDTO)
                .toList();
    }

    public SolutionDTO getProblemById(int appointmentId) {
        Optional<Solution> optionalProblem = solutionRepository.findById(appointmentId);
        if (optionalProblem.isPresent()) {
            return convertProblemToDTO(optionalProblem.get());
        }
        throw new NotFoundException("Problem not found with id: " + appointmentId);
    }

    public TestRequestDTO sendTestToQueue(int appointmentId) {
        Optional<Solution> existingProblemOptional = getSolutionRepository().findById(appointmentId);
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

    public void processTestRequest(TestRequestDTO testRequestDTO) {
        Optional<Solution> problem = solutionRepository.findById(testRequestDTO.getId());
        if (problem.isPresent()) {
            runTests(testRequestDTO, problem.get());
        } else {
            testRequestDTO.setOutput("404, Solution with id " + testRequestDTO.getId() + "not found");
            redisTemplate.opsForValue().set("solution:" + testRequestDTO.getId(), testRequestDTO);
        }
    }

    private void runTests(TestRequestDTO testRequestDTO, Solution solution) {

        Optional<StudentAppointment> appointment = studentAppointmentsRepository.findById(testRequestDTO.getId());

        if (appointment.isEmpty()) {
            testRequestDTO.setOutput("404, Appointment for solution with id " + testRequestDTO.getId() + " not found");
            LOGGER.error("404, Appointment with id " + testRequestDTO.getId() + "not found");
            redisTemplate.opsForValue().set("solution:" + testRequestDTO.getId(), testRequestDTO);

        } else {

            List<Test> testList = testsRepository.findAllByTaskIdAndDeletedFalse(appointment.get().getTaskId());

            int totalTests = testList.size();
            testRequestDTO.setTotalTests(totalTests);
            List<Integer> failedTestIds = new ArrayList<>();
            try {
                File cCode = CCodeGenerator.generateCCode(convertProblemToCodeRequest(solution));
                for (Test test : testList) {
                    String[] inputValues = test.getInput().split(" ");
                    ExecutionResult executionResult = cCodeExecutor.executeCode(cCode, inputValues);

                    if (executionResult.isError()) {
                        testRequestDTO.setOutput(executionResult.error());
                        failedTestIds.add(test.getId());
                        testRequestDTO.setError(true);
                        LOGGER.error(executionResult.error());
                        redisTemplate.opsForValue().set("solution:" + testRequestDTO.getId(), testRequestDTO);
                    } else {
                        testRequestDTO.setOutput(executionResult.output());
                        if (!handleTestResult(executionResult.result(), test, testRequestDTO)) {
                            failedTestIds.add(test.getId());
                        }
                    }
                }
                testRequestDTO.setFailedTestIds(failedTestIds);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LOGGER.info("Load to cache " + testRequestDTO);
            redisTemplate.opsForValue().set("solution:" + testRequestDTO.getId(), testRequestDTO);
        }
    }

    private boolean handleTestResult(String result, Test test, TestRequestDTO testRequestDTO) {
        int expectedOutput = Integer.parseInt(test.getOutput());
        int actualOutput = Integer.parseInt(result.trim());

        if (expectedOutput == actualOutput) {
            testRequestDTO.incrementTestPassed();
            return true;
        } else {
            return false;
        }
    }

    public ServiceResult<SolutionDTO> saveProblem(SaveSolutionDTO saveSolutionDTO) throws IOException, InterruptedException {

        String funcName = saveSolutionDTO.getFuncName();
        String code = saveSolutionDTO.getCode();
        String language = saveSolutionDTO.getLanguage();
        int appointmentId = saveSolutionDTO.getAppointmentId();

        if (!isValidLanguage(language)) {
            throw new BadRequestException("Unsupported programming language: " + language);
        }

        Optional<Solution> existingProblemOptional = solutionRepository.findById(appointmentId);

        if (existingProblemOptional.isPresent()) {
            Solution existingSolution = existingProblemOptional.get();
            updateProblem(existingSolution, language, code, funcName);
            LOGGER.info("Solution with id=" + appointmentId + "successfully updated");
            return new ServiceResult<>(convertProblemToDTO(existingSolution), false);
        } else {
            CAnalyzer.FunctionInfo result = analyzeCCode(code, funcName);

            Solution newSolution = new Solution(appointmentId, language, code, funcName, result.getReturnType(), result.getArguments());

            solutionRepository.save(newSolution);
            LOGGER.info("Solution with id=" + appointmentId + "successfully saved");
            return new ServiceResult<>(convertProblemToDTO(newSolution), true);
        }
    }

    public String deleteProblemById(int appointmentId) {
        Optional<Solution> existingProblemOptional = getSolutionRepository().findById(appointmentId);
        if (existingProblemOptional.isPresent()) {
            solutionRepository.deleteById(appointmentId);
            return "Problem with id " + appointmentId + " deleted successfully";
        } else {
            throw new NotFoundException("Problem with id=" + appointmentId + " not found");
        }

    }

    private SolutionDTO convertProblemToDTO(Solution solution) {
        SolutionDTO solutionDTO = new SolutionDTO();
        solutionDTO.setAppointmentId(solution.getAppointmentId());
        solutionDTO.setLanguage(solution.getLanguage());
        solutionDTO.setCode(solution.getCode());
        solutionDTO.setFunctionName(solution.getFunctionName());
        solutionDTO.setReturnType(solution.getReturnType());
        solutionDTO.setArguments(solution.getArguments());
        return solutionDTO;
    }

    private CodeRequest convertProblemToCodeRequest(Solution solution) {
        return new CodeRequest(solution.getCode(), solution.getFunctionName(), solution.getReturnType(), solution.getArguments());
    }

    private boolean isValidLanguage(String programmingLanguage) {
        return List.of("c", "cpp", "java").contains(programmingLanguage);
    }

    private void updateProblem(Solution existingSolution, String language, String code, String funcName) throws IOException, InterruptedException {
        CAnalyzer.FunctionInfo result = analyzeCCode(code, funcName);
        existingSolution.setLanguage(language);
        existingSolution.setCode(code);
        existingSolution.setFunctionName(funcName);
        existingSolution.setReturnType(result.getReturnType());
        existingSolution.setArguments(result.getArguments());
        System.out.println("RES ARGS: " + result.getArguments());
        Update update = new Update();
        update.set("programmingLanguage", language);
        update.set("code", code);
        update.set("functionName", funcName);
        update.set("returnType", result.getReturnType());
        update.set("arguments", result.getArguments());

        Query query = Query.query(Criteria.where("_id").is(existingSolution.getAppointmentId()));
        mongoTemplate.updateFirst(query, update, Solution.class);
    }

    public List<SolutionDTO> getCodesByAppointmentIds(List<Integer> appointmentIds) {
        List<SolutionDTO> result = new ArrayList<>();
        for (Integer appointmentId : appointmentIds) {
            Optional<Solution> problemOptional = solutionRepository.findById(appointmentId);
            if (problemOptional.isPresent()) {
                Solution solution = problemOptional.get();
                SolutionDTO solutionDTO = new SolutionDTO(
                        appointmentId,
                        solution.getLanguage(),
                        solution.getCode(),
                        solution.getFunctionName(),
                        solution.getReturnType(),
                        solution.getArguments()
                );
                result.add(solutionDTO);
            }
        }
        return result;
    }
}
