package com.teenspirit.coderunnerhub.controller;

import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import com.teenspirit.coderunnerhub.dto.SolutionDTO;
import com.teenspirit.coderunnerhub.model.Problem;
import com.teenspirit.coderunnerhub.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problems")
public class ProblemController {
    private final ProblemService problemService;

    @Autowired
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
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

    @PostMapping
    public ResponseEntity<ProblemDTO> createProblem( @RequestBody SolutionDTO solution) {
        ProblemDTO createdProblem = problemService.saveProblem(solution);
        return new ResponseEntity<>(createdProblem, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable int id) {
        problemService.deleteProblemById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
