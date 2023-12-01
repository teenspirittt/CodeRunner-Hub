package com.teenspirit.coderunnerhub.service;

import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import com.teenspirit.coderunnerhub.dto.ServiceResult;
import com.teenspirit.coderunnerhub.dto.SolutionDTO;
import com.teenspirit.coderunnerhub.exceptions.BadRequestException;
import com.teenspirit.coderunnerhub.exceptions.NotFoundException;
import com.teenspirit.coderunnerhub.model.Problem;
import com.teenspirit.coderunnerhub.repository.ProblemsRepository;
import com.teenspirit.coderunnerhub.util.CAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
                .map(problem -> convertToDTO(problem))
                .toList();
    }

    public ProblemDTO getProblemById(int appointmentId) {
        Optional<Problem> optionalProblem = problemRepository.findById(appointmentId);
        if (optionalProblem.isPresent()) {
            return convertToDTO(optionalProblem.get());
        }
        throw new NotFoundException("Problem not found with id: " + appointmentId);
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
            return new ServiceResult<>(convertToDTO(existingProblem), true);
        } else {
            CAnalyzer.FunctionInfo result = analyzeCCode(code, funcName);

            Problem newProblem = new Problem(appointmentId, language, code, funcName, result.getReturnType(), result.getArguments());
            problemRepository.save(newProblem);
            return new ServiceResult<>(convertToDTO(newProblem), false);
        }
    }

    public void deleteProblemById(int appointmentId) {
        problemRepository.deleteById(appointmentId);
    }

    private ProblemDTO convertToDTO(Problem problem) {
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setAppointmentId(problem.getAppointmentId());
        problemDTO.setLanguage(problem.getProgrammingLanguage());
        problemDTO.setCode(problem.getCode());
        problemDTO.setFunctionName(problem.getFunctionName());
        problemDTO.setReturnType(problem.getReturnType());
        problemDTO.setArguments(problem.getArguments());
        return problemDTO;
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

}
