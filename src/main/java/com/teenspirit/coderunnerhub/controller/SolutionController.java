package com.teenspirit.coderunnerhub.controller;

import com.teenspirit.coderunnerhub.dto.*;
import com.teenspirit.coderunnerhub.service.SolutionService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Тестирование решения"
    )
    @PostMapping("/test/{id}")
    public Response<TestRequestDTO> testProblem(@PathVariable int id) {
        return Response.ok(solutionService.sendTestToQueue(id));
    }

    @Operation(
            summary = "Сохранение решения",
            description = "Новое решение сохраняется, существующее обновляется"
    )
    @PostMapping("/save")
    public Response<SolutionDTO> saveProblem(@RequestBody SaveSolutionDTO solution) throws IOException, InterruptedException {
        ServiceResult<SolutionDTO> serviceResult = solutionService.saveSolution(solution);
        if (serviceResult.updated()) {
            return Response.ok(serviceResult.data());
        } else {
            return Response.created(serviceResult.data());
        }
    }

    @Operation(
            summary = "Получение всех решений",
            description = "Новое решение сохраняется, существующее обновляется"
    )
    @GetMapping
    public Response<List<SolutionDTO>> getAllSolutions() {
        List<SolutionDTO> problems = solutionService.getAllSolutions();
        return Response.ok(problems);
    }

    @Operation(
            summary = "Получение решения по идентификатору"
    )
    @GetMapping("/{id}")
    public Response<SolutionDTO> getSolutionById(@PathVariable int id) {
        SolutionDTO problem = solutionService.getSolutionById(id);
        return Response.ok(problem);
    }

    @Operation(
            summary = "Удаление решения по идентификатору"
    )
    @DeleteMapping("/{id}")
    public Response<String> deleteProblem(@PathVariable int id) {
        return Response.ok(solutionService.deleteSolutionById(id));
    }

    @Operation(
            summary = "Получение списка кодов решений по идентификаторам",
            description = "Если решения нет, то в теле ответа ничего не будет"
    )
    @PostMapping("/codes")
    public Response<List<SolutionDTO>> getCodesByAppointmentIds(@RequestBody List<Integer> appointmentIds) {
        List<SolutionDTO> result = solutionService.getCodesByAppointmentIds(appointmentIds);
        return Response.ok(result);
    }
}
