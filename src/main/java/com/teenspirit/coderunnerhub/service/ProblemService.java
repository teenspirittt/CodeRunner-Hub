package com.teenspirit.coderunnerhub.service;

import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import com.teenspirit.coderunnerhub.dto.SolutionDTO;
import com.teenspirit.coderunnerhub.exceptions.NotFoundException;
import com.teenspirit.coderunnerhub.model.Problem;
import com.teenspirit.coderunnerhub.repository.ProblemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProblemService {
    private final ProblemsRepository problemRepository;

    @Autowired
    public ProblemService(ProblemsRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public List<ProblemDTO> getAllProblems() {
        return problemRepository.findAll()
                .stream()
                .map(problem -> convertToDTO(problem))
                .toList();
    }

    public ProblemDTO getProblemById(int appointmentId) {
        Optional<Problem> optionalProblem =  problemRepository.findById(appointmentId);
        if (optionalProblem.isPresent()) {
            return convertToDTO(optionalProblem.get());
        } throw new NotFoundException("Problem not found with id: " + appointmentId);
    }

    public ProblemDTO saveProblem(SolutionDTO solutionDTO) {
        return null;
        //return convertToDTO(problemRepository.save(convertToEntity(problemDTO)));
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


}
