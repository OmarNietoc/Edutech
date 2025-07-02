package com.edutech.evaluations.service;

import com.edutech.evaluations.exception.RecursoNoEncontradoException;
import com.edutech.evaluations.model.Evaluation;
import com.edutech.evaluations.repository.EvaluacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluacionServiceImplTest {

    private EvaluacionRepository evaluacionRepository;
    private EvaluacionServiceImpl evaluacionService;

    @BeforeEach
    void setUp() {
        evaluacionRepository = mock(EvaluacionRepository.class);
        evaluacionService = new EvaluacionServiceImpl(evaluacionRepository);
    }

    @Test
    void guardarEvaluacion_deberiaGuardarYRetornar() {
        Evaluation evaluacion = new Evaluation();
        when(evaluacionRepository.save(evaluacion)).thenReturn(evaluacion);

        Evaluation resultado = evaluacionService.guardarEvaluacion(evaluacion);

        assertEquals(evaluacion, resultado);
        verify(evaluacionRepository).save(evaluacion);
    }

    @Test
    void obtenerEvaluacionesPorAsignatura_conResultados() {
        String asignatura = "Matemáticas";
        List<Evaluation> lista = List.of(new Evaluation());
        when(evaluacionRepository.findByAsignatura(asignatura)).thenReturn(lista);

        List<Evaluation> resultado = evaluacionService.obtenerEvaluacionesPorAsignatura(asignatura);

        assertEquals(lista, resultado);
        verify(evaluacionRepository).findByAsignatura(asignatura);
    }

    @Test
    void obtenerEvaluacionesPorAsignatura_sinResultados_lanzaExcepcion() {
        String asignatura = "Física";
        when(evaluacionRepository.findByAsignatura(asignatura)).thenReturn(Collections.emptyList());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class, () -> {
            evaluacionService.obtenerEvaluacionesPorAsignatura(asignatura);
        });

        assertTrue(ex.getMessage().contains("No se encontraron evaluaciones"));
    }

    @Test
    void obtenerEvaluacionPorId_existente() {
        Evaluation evaluacion = new Evaluation();
        evaluacion.setId(1L);
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));

        Evaluation resultado = evaluacionService.obtenerEvaluacionPorId(1L);

        assertEquals(evaluacion, resultado);
    }

    @Test
    void obtenerEvaluacionPorId_inexistente_lanzaExcepcion() {
        when(evaluacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> {
            evaluacionService.obtenerEvaluacionPorId(99L);
        });
    }

    @Test
    void actualizarEvaluacion_deberiaActualizarYRetornar() {
        Evaluation existente = new Evaluation();
        existente.setId(1L);
        existente.setNombreAlumno("Antiguo");
        existente.setAsignatura("Historia");
        existente.setNota(4.0);

        Evaluation nuevo = new Evaluation();
        nuevo.setNombreAlumno("Nuevo");
        nuevo.setAsignatura("Ciencias");
        nuevo.setNota(6.0);

        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(evaluacionRepository.save(any(Evaluation.class))).thenAnswer(inv -> inv.getArgument(0));

        Evaluation resultado = evaluacionService.actualizarEvaluacion(1L, nuevo);

        assertEquals("Nuevo", resultado.getNombreAlumno());
        assertEquals("Ciencias", resultado.getAsignatura());
        assertEquals(6.0, resultado.getNota());
    }

    @Test
    void eliminarEvaluacion_deberiaEliminar() {
        evaluacionService.eliminarEvaluacion(1L);
        verify(evaluacionRepository).deleteById(1L);
    }

    @Test
    void obtenerTodasLasEvaluaciones_retornaLista() {
        List<Evaluation> lista = Arrays.asList(new Evaluation(), new Evaluation());
        when(evaluacionRepository.findAll()).thenReturn(lista);

        List<Evaluation> resultado = evaluacionService.obtenerTodasLasEvaluaciones();

        assertEquals(2, resultado.size());
    }
}
