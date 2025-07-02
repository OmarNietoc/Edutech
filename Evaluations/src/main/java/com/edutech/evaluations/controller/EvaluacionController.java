package com.edutech.evaluations.controller;

import com.edutech.evaluations.model.Evaluation;
import com.edutech.evaluations.service.EvaluacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/edutech/evaluations")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    public EvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @Operation(
            summary = "Crear evaluación",
            description = "Registra una nueva evaluación con los datos proporcionados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para crear evaluación")
    })

    @PostMapping
    public Evaluation crearEvaluacion(@Valid @RequestBody Evaluation evaluacion) {
        return evaluacionService.guardarEvaluacion(evaluacion);
    }

    @Operation(
            summary = "Obtener evaluaciones por asignatura",
            description = "Devuelve una lista de evaluaciones asociadas a una asignatura específica."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluaciones encontradas"),
            @ApiResponse(responseCode = "404", description = "No se encontraron evaluaciones para la asignatura")
    })

    @GetMapping("/asignatura/{asignatura}")
    public List<Evaluation> obtenerPorAsignatura(@PathVariable String asignatura) {
        return evaluacionService.obtenerEvaluacionesPorAsignatura(asignatura);
    }

    @Operation(
            summary = "Listar todas las evaluaciones",
            description = "Devuelve una lista completa de todas las evaluaciones registradas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluaciones listadas exitosamente")
    })

    @GetMapping
    public List<Evaluation> obtenerTodas() {
        return evaluacionService.obtenerTodasLasEvaluaciones();
    }

    @Operation(
            summary = "Obtener evaluación por ID",
            description = "Devuelve la evaluación correspondiente al ID especificado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación encontrada"),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })

    @GetMapping("/{id}")
    public Evaluation obtenerPorId(@PathVariable Long id) {
        return evaluacionService.obtenerEvaluacionPorId(id);
    }

    @Operation(
            summary = "Actualizar evaluación",
            description = "Actualiza los datos de una evaluación existente mediante su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })

    @PutMapping("/{id}")
    public Evaluation actualizar(@PathVariable Long id, @Valid @RequestBody Evaluation evaluacion) {
        return evaluacionService.actualizarEvaluacion(id, evaluacion);
    }

    @Operation(
            summary = "Eliminar evaluación",
            description = "Elimina una evaluación existente mediante su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        evaluacionService.eliminarEvaluacion(id);
    }

}
