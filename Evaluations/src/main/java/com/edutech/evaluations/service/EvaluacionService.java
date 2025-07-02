package com.edutech.evaluations.service;

import com.edutech.evaluations.model.Evaluation;
import java.util.List;

public interface EvaluacionService {
    Evaluation guardarEvaluacion(Evaluation evaluacion);
    List<Evaluation> obtenerEvaluacionesPorAsignatura(String asignatura);
    Evaluation obtenerEvaluacionPorId(Long id);
    Evaluation actualizarEvaluacion(Long id, Evaluation evaluacion);
    void eliminarEvaluacion(Long id);
    List<Evaluation> obtenerTodasLasEvaluaciones();
}
