package com.teenspirit.coderunnerhub.controller;

import com.teenspirit.coderunnerhub.dto.ProblemDTO;
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

    @PostMapping("/execute")
    public ResponseEntity<ExecuteResponse> executeProblem(@PathVariable int id) throws IOException, InterruptedException {

        ServiceResult<ExecuteResponse> serviceResult = problemService.executeProblem(id);

        if (serviceResult.isUpdated()) {
            return new ResponseEntity<>(serviceResult.getData(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(serviceResult.getData(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<ProblemDTO> saveProblem(@RequestBody SolutionDTO solution) throws IOException, InterruptedException {
        ServiceResult<ProblemDTO> serviceResult = problemService.saveProblem(solution);
        if (serviceResult.isUpdated()) {
            return new ResponseEntity<>(serviceResult.getData(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(serviceResult.getData(), HttpStatus.CREATED);
        }
    }

    @GetMapping
    public ResponseEntity<List<ProblemDTO>> getAllProblems() {
        List<ProblemDTO> problems = problemService.getAllProblems();
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemDTO> getProblemById(@PathVariable int id) {
        ProblemDTO problem = problemService.getProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable int id) {
        problemService.deleteProblemById(id);
        Optional<Problem> existingProblemOptional = problemService.getProblemRepository().findById(id);
        if(existingProblemOptional.isPresent()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/codes")
    public ResponseEntity<List<Map<String, Object>>> getCodesByAppointmentIds(@RequestBody List<Integer> appointmentIds) {
        List<Map<String, Object>> result = problemService.getCodesByAppointmentIds(appointmentIds);
        return ResponseEntity.ok(result);
    }
}
