package com.edutech.evaluations.service;

import com.edutech.evaluations.model.Evaluation;
import com.edutech.evaluations.exception.RecursoNoEncontradoException;
import com.edutech.evaluations.repository.EvaluacionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EvaluacionServiceImpl implements EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;

    public EvaluacionServiceImpl(EvaluacionRepository evaluacionRepository) {
        this.evaluacionRepository = evaluacionRepository;
    }

    @Override
    public Evaluation guardarEvaluacion(Evaluation evaluacion) {
        return evaluacionRepository.save(evaluacion);
    }

    @Override
    public List<Evaluation> obtenerEvaluacionesPorAsignatura(String asignatura) {
        List<Evaluation> evaluaciones = evaluacionRepository.findByAsignatura(asignatura);
        if (evaluaciones.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron evaluaciones para la asignatura: " + asignatura);
        }
        return evaluaciones;
    }


    @Override
    public Evaluation obtenerEvaluacionPorId(Long id) {
        return evaluacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evaluación con ID " + id + " no fue encontrada."));
    }




    @Override
    public Evaluation actualizarEvaluacion(Long id, Evaluation evaluacion) {
        Evaluation evaluacionExistente = obtenerEvaluacionPorId(id);
        evaluacionExistente.setNombreAlumno(evaluacion.getNombreAlumno());
        evaluacionExistente.setAsignatura(evaluacion.getAsignatura());
        evaluacionExistente.setNota(evaluacion.getNota());
        return evaluacionRepository.save(evaluacionExistente);
    }


    @Override
    public void eliminarEvaluacion(Long id) {
        evaluacionRepository.deleteById(id);
    }

    @Override
    public List<Evaluation> obtenerTodasLasEvaluaciones() {
        return evaluacionRepository.findAll();
    }
}

