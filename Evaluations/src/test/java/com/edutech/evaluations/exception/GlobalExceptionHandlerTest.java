package com.edutech.evaluations.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_deberiaRetornar404() {
        String mensaje = "Evaluación no encontrada";
        RecursoNoEncontradoException ex = new RecursoNoEncontradoException(mensaje);

        ResponseEntity<String> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(mensaje, response.getBody());
    }

    @Test
    void handleValidationExceptions_deberiaRetornarErroresDeCampo() {
        // Simula un FieldError
        FieldError error1 = new FieldError("evaluacion", "nombreAlumno", "no debe estar vacío");
        FieldError error2 = new FieldError("evaluacion", "nota", "debe ser mayor que 1");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(List.of(error1, error2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errores = response.getBody();

        assertNotNull(errores);
        assertEquals(2, errores.size());
        assertEquals("no debe estar vacío", errores.get("nombreAlumno"));
        assertEquals("debe ser mayor que 1", errores.get("nota"));
    }
}

