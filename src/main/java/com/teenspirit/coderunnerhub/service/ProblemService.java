package com.teenspirit.coderunnerhub.service;

import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import com.teenspirit.coderunnerhub.dto.ServiceResult;
import com.teenspirit.coderunnerhub.dto.SolutionDTO;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.teenspirit.coderunnerhub.util.CAnalyzer.analyzeCCode;

@Service
public class ProblemService {


    private final ProblemsRepository problemRepository;
    private final MongoTemplate mongoTemplate;
    @Autowired
    public ProblemService(ProblemsRepository problemRepository, MongoTemplate mongoTemplate) {
        this.problemRepository = problemRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<ProblemDTO> getAllProblems() {
        return problemRepository.findAll()
                .stream()
                .map(problem -> convertProblemToDTO(problem))
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

    public ServiceResult<ExecuteResponse> saveProblem(SolutionDTO solutionDTO) throws IOException, InterruptedException {

        String funcName = solutionDTO.getFuncName();
        String code = solutionDTO.getCode();
        String language = solutionDTO.getLanguage();
        int appointmentId = solutionDTO.getAppointmentId();

        if (!isValidLanguage(language)) {
            throw new BadRequestException("Unsupported programming language: " + language);
        }

        Optional<Problem> existingProblemOptional = problemRepository.findById(appointmentId);
        CCodeExecutor cCodeExecutor = new CCodeExecutor();


        if (existingProblemOptional.isPresent()) {
            Problem existingProblem = existingProblemOptional.get();
            updateProblem(existingProblem, language, code, funcName);


            ExecuteResponse executeResponse = cCodeExecutor.executeCCode(convertProblemToCodeRequest(existingProblem));

            return new ServiceResult<>(executeResponse, true);
        } else {
            CAnalyzer.FunctionInfo result = analyzeCCode(code, funcName);

            Problem newProblem = new Problem(appointmentId, language, code, funcName, result.getReturnType(), result.getArguments());
            problemRepository.save(newProblem);

            ExecuteResponse executeResponse = cCodeExecutor.executeCCode(convertProblemToCodeRequest(newProblem));

            return new ServiceResult<>(executeResponse, false);
        }
    }

    public void deleteProblemById(int appointmentId) {
        problemRepository.deleteById(appointmentId);
    }

    private ProblemDTO convertProblemToDTO(Problem problem) {
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setAppointmentId(problem.getAppointmentId());
        problemDTO.setLanguage(problem.getProgrammingLanguage());
        problemDTO.setCode(problem.getCode());
        problemDTO.setFunctionName(problem.getFunctionName());
        problemDTO.setReturnType(problem.getReturnType());
        problemDTO.setArguments(problem.getArguments());
        return problemDTO;
    }

    private CodeRequest convertProblemToCodeRequest(Problem problem) {
        return new CodeRequest(problem.getCode(), problem.getFunctionName(),problem.getReturnType(), problem.getArguments());
    }

    private Problem convertToEntity(ProblemDTO problemDTO) {
        Problem problem = new Problem();
        problem.setAppointmentId(problemDTO.getAppointmentId());
        problem.setProgrammingLanguage(problemDTO.getLanguage());
        problem.setCode(problemDTO.getCode());
        problem.setFunctionName(problemDTO.getFunctionName());
        problem.setReturnType(problemDTO.getReturnType());
        problem.setArguments(problemDTO.getArguments());
        return problem;
    }

    private boolean isValidLanguage(String programmingLanguage) {
        return List.of("c", "cpp", "java").contains(programmingLanguage);
    }

    private void updateProblem(Problem existingProblem, String language, String code, String funcName) throws IOException, InterruptedException {
        CAnalyzer.FunctionInfo result = analyzeCCode(code, funcName);
        existingProblem.setProgrammingLanguage(language);
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

    public List<Map<String, Object>> getCodesByAppointmentIds(List<Integer> appointmentIds) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Integer appointmentId : appointmentIds) {
            Map<String, Object> item = new HashMap<>();
            Optional<Problem> problemOptional = problemRepository.findById(appointmentId);
            item.put("appointmentId", appointmentId);
            if (problemOptional.isPresent()) {
                // Если проблема существует, добавляем код в мапу
                item.put("code", problemOptional.get().getCode());
            } else {
                // Если проблема не найдена, добавляем null
                item.put("code", null);
            }
            result.add(item);
        }
        return result;
    }
}
