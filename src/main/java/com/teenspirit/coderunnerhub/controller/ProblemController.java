package com.teenspirit.coderunnerhub.controller;

import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import com.teenspirit.coderunnerhub.dto.Response;
import com.teenspirit.coderunnerhub.dto.ServiceResult;
import com.teenspirit.coderunnerhub.dto.SolutionDTO;
import com.teenspirit.coderunnerhub.model.ExecuteResponse;
import com.teenspirit.coderunnerhub.model.Problem;
import com.teenspirit.coderunnerhub.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/problems")
public class ProblemController {
    private final ProblemService problemService;

    @Autowired
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping("/execute/{id}")
    public Response<ExecuteResponse> executeProblem(@PathVariable int id) throws IOException, InterruptedException {

        ServiceResult<ExecuteResponse> serviceResult = problemService.executeProblem(id);

        return Response.ok(serviceResult.data());
    }

    @PostMapping("/save")
    public Response<ProblemDTO> saveProblem(@RequestBody SolutionDTO solution) throws IOException, InterruptedException {
        ServiceResult<ProblemDTO> serviceResult = problemService.saveProblem(solution);
        if (serviceResult.updated()) {
            return Response.ok(serviceResult.data());
        } else {
            return Response.created(serviceResult.data());
        }
    }

    @GetMapping
    public Response<List<ProblemDTO>> getAllProblems() {
        List<ProblemDTO> problems = problemService.getAllProblems();
        return Response.ok(problems);
    }

    @GetMapping("/{id}")
    public Response<ProblemDTO> getProblemById(@PathVariable int id) {
        ProblemDTO problem = problemService.getProblemById(id);
        return Response.ok(problem);
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteProblem(@PathVariable int id) {
        problemService.deleteProblemById(id);
        Optional<Problem> existingProblemOptional = problemService.getProblemRepository().findById(id);
        if(existingProblemOptional.isPresent()){
            return Response.noContent();
        } else {
            return Response.createError(HttpStatus.NOT_FOUND, "Problem with id=" + id + " not found");
        }
    }

    @PostMapping("/codes")
    public Response<List<ProblemDTO>> getCodesByAppointmentIds(@RequestBody List<Integer> appointmentIds) {
        List<ProblemDTO> result = problemService.getCodesByAppointmentIds(appointmentIds);
        return Response.ok(result);
    }
}
