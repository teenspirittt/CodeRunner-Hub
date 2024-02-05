package com.teenspirit.coderunnerhub.controller;

import com.teenspirit.coderunnerhub.dto.*;
import com.teenspirit.coderunnerhub.exceptions.InternalServerErrorException;
import com.teenspirit.coderunnerhub.model.Problem;
import com.teenspirit.coderunnerhub.service.ProblemService;
import com.teenspirit.coderunnerhub.util.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/problems")
public class ProblemController {
    private final ProblemService problemService;
    private final MessageSender messageSender;
    private final RedisTemplate<String, TestRequestDTO> redisTemplate;

    @Autowired
    public ProblemController(ProblemService problemService, MessageSender messageSender, RedisTemplate<String, TestRequestDTO> redisTemplate) {
        this.problemService = problemService;
        this.messageSender = messageSender;
        this.redisTemplate = redisTemplate;
    }

    @Async
    public CompletableFuture<TestRequestDTO> waitForTestResultsAsync(int id) {
        try { // todo: test this
            for (int i = 0; i < 30; i++) {
                Thread.sleep(1000);

                TestRequestDTO result = redisTemplate.opsForValue().get("solution:" + id);
                if (result != null) {
                    return CompletableFuture.completedFuture(result);
                }
            }

            return CompletableFuture.completedFuture(null);
        } catch (InterruptedException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @PostMapping("/execute/{id}")
    public Response<TestRequestDTO> executeProblem(@PathVariable int id) throws IOException, InterruptedException {
        try {
            messageSender.sendMessage(id);
            CompletableFuture<TestRequestDTO> result = waitForTestResultsAsync(id);
            return Response.ok(result.get());
        } catch (Exception e) {
            return Response.internalServerError("Internal Server Error");
        }
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
