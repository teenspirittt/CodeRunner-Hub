package com.teenspirit.coderunnerhub.service;

import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import com.teenspirit.coderunnerhub.dto.ServiceResult;
import com.teenspirit.coderunnerhub.dto.SolutionDTO;
import com.teenspirit.coderunnerhub.dto.TestRequestDTO;
import com.teenspirit.coderunnerhub.exceptions.BadRequestException;
import com.teenspirit.coderunnerhub.exceptions.NotFoundException;
import com.teenspirit.coderunnerhub.model.CodeRequest;
import com.teenspirit.coderunnerhub.model.ExecuteResponse;
import com.teenspirit.coderunnerhub.model.Problem;
import com.teenspirit.coderunnerhub.repository.ProblemsRepository;
import com.teenspirit.coderunnerhub.util.CAnalyzer;
import com.teenspirit.coderunnerhub.util.CCodeExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.teenspirit.coderunnerhub.util.CAnalyzer.analyzeCCode;

@Service
public class ProblemService {


    private final ProblemsRepository problemRepository;
    private final MongoTemplate mongoTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ProblemService(ProblemsRepository problemRepository, MongoTemplate mongoTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.problemRepository = problemRepository;
        this.mongoTemplate = mongoTemplate;
        this.redisTemplate = redisTemplate;
    }

    public List<ProblemDTO> getAllProblems() {
        return problemRepository.findAll()
                .stream()
                .map(this::convertProblemToDTO)
                .toList();
    }

    public ProblemsRepository getProblemRepository() {
        return problemRepository;
    }

    public ProblemDTO getProblemById(int appointmentId) {
        Optional<Problem> optionalProblem = problemRepository.findById(appointmentId);
        if (optionalProblem.isPresent()) {
            return convertProblemToDTO(optionalProblem.get());
        }
        throw new NotFoundException("Problem not found with id: " + appointmentId);
    }


    public void processTestRequest(TestRequestDTO testRequestDTO) {
        Integer cachedResult = (Integer) redisTemplate.opsForValue().get("solution:" + testRequestDTO.getHashCode());

        if (cachedResult != null) {
            int totalTests = 10; // todo get real total tests from db
            new TestRequestDTO(cachedResult, totalTests, testRequestDTO.getId());
        } else {
            Optional<Problem> problem = problemRepository.findById(testRequestDTO.getId());
            if (problem.isPresent()) {
                runTests(testRequestDTO.getId());
            } else {
                throw new NotFoundException("Problem not found with id: " + testRequestDTO.getId());
            }
        }
    }

    private void runTests(int id) {
        int passedTests = 5;
        int totalTests = 10;
        redisTemplate.opsForValue().set("solution:" + id, passedTests);


        new TestRequestDTO(passedTests, totalTests, id);
    }



    public ServiceResult<ExecuteResponse> executeProblem(int id) throws IOException, InterruptedException {

        Optional<Problem> existingProblemOptional = problemRepository.findById(id);

        if (existingProblemOptional.isEmpty()) {
            throw new NotFoundException("Problem with id=" + id + " not found");
        }

        String funcName = existingProblemOptional.get().getFunctionName();
        String code = existingProblemOptional.get().getCode();
        String language = existingProblemOptional.get().getLanguage();


        if (!isValidLanguage(language)) {
            throw new BadRequestException("Unsupported programming language: " + language);
        }

        CCodeExecutor cCodeExecutor = new CCodeExecutor();
        Problem existingProblem = existingProblemOptional.get();
        updateProblem(existingProblem, language, code, funcName);
        ExecuteResponse executeResponse = cCodeExecutor.executeCCode(convertProblemToCodeRequest(existingProblem));

        return new ServiceResult<>(executeResponse, true);
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

    public void deleteProblemById(int appointmentId) {
        problemRepository.deleteById(appointmentId);
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
