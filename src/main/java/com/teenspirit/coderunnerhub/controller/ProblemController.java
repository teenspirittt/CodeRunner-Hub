package com.teenspirit.coderunnerhub.controller;

import com.teenspirit.coderunnerhub.dto.*;
import com.teenspirit.coderunnerhub.exceptions.InternalServerErrorException;
import com.teenspirit.coderunnerhub.exceptions.NotFoundException;
import com.teenspirit.coderunnerhub.model.Problem;
import com.teenspirit.coderunnerhub.service.ProblemService;
import com.teenspirit.coderunnerhub.util.HashCodeGenerator;
import com.teenspirit.coderunnerhub.util.MessageSender;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/problems")
public class ProblemController {
    private final ProblemService problemService;


    @Autowired
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping("/test/{id}")
    public Response<TestRequestDTO> testProblem(@PathVariable int id) {
            return Response.ok(problemService.sendTestToQueue(id));
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
    public Response<String> deleteProblem(@PathVariable int id) {
        return Response.ok(problemService.deleteProblemById(id));
    }

    @PostMapping("/codes")
    public Response<List<ProblemDTO>> getCodesByAppointmentIds(@RequestBody List<Integer> appointmentIds) {
        List<ProblemDTO> result = problemService.getCodesByAppointmentIds(appointmentIds);
        return Response.ok(result);
    }
}
