package com.teenspirit.coderunnerhub.controller;

import com.teenspirit.coderunnerhub.dto.*;
import com.teenspirit.coderunnerhub.service.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/solutions")
public class SolutionController {
    private final SolutionService solutionService;


    @Autowired
    public SolutionController(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    @PostMapping("/test/{id}")
    public Response<TestRequestDTO> testProblem(@PathVariable int id) {
            return Response.ok(solutionService.sendTestToQueue(id));
    }

    @PostMapping("/save")
    public Response<SolutionDTO> saveProblem(@RequestBody SaveSolutionDTO solution) throws IOException, InterruptedException {
        ServiceResult<SolutionDTO> serviceResult = solutionService.saveProblem(solution);
        if (serviceResult.updated()) {
            return Response.ok(serviceResult.data());
        } else {
            return Response.created(serviceResult.data());
        }
    }

    @GetMapping
    public Response<List<SolutionDTO>> getAllProblems() {
        List<SolutionDTO> problems = solutionService.getAllProblems();
        return Response.ok(problems);
    }

    @GetMapping("/{id}")
    public Response<SolutionDTO> getProblemById(@PathVariable int id) {
        SolutionDTO problem = solutionService.getProblemById(id);
        return Response.ok(problem);
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteProblem(@PathVariable int id) {
        return Response.ok(solutionService.deleteProblemById(id));
    }

    @PostMapping("/codes")
    public Response<List<SolutionDTO>> getCodesByAppointmentIds(@RequestBody List<Integer> appointmentIds) {
        List<SolutionDTO> result = solutionService.getCodesByAppointmentIds(appointmentIds);
        return Response.ok(result);
    }
}
