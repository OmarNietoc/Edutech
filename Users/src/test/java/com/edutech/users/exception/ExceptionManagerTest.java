package com.edutech.users.exception;

import com.edutech.users.controller.response.MessageResponse;
import com.edutech.users.exception.ConflictException;
import com.edutech.users.exception.ExceptionManager;
import com.edutech.users.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionManagerTest {

    private ExceptionManager exceptionManager;

    @BeforeEach
    void setUp() {
        exceptionManager = new ExceptionManager();
    }

    @Test
    void handleConflict_returnsConflictStatus() {
        ConflictException exception = new ConflictException("Conflicto detectado");
        ResponseEntity<?> response = exceptionManager.handleConflict(exception);

        assertEquals(409, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals("Conflicto detectado", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleResourceNotFound_returnsNotFoundStatus() {
        ResourceNotFoundException exception = new ResourceNotFoundException("No encontrado");
        ResponseEntity<?> response = exceptionManager.handleResourceNotFound(exception);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("No encontrado", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleIllegalArgument_returnsBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Dato inválido");
        ResponseEntity<?> response = exceptionManager.handleIllegalArgument(exception);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Dato inválido", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleConstraintViolation_returnsBadRequest() {
        ConstraintViolationException exception = new ConstraintViolationException("Error de validación", Set.of());
        ResponseEntity<?> response = exceptionManager.handleConstraintViolation(exception);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error de validación", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleException_returnsInternalServerError() {
        Exception exception = new Exception("Falla inesperada");
        ResponseEntity<?> response = exceptionManager.handleException(exception);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(((MessageResponse) response.getBody()).getMessage().contains("Falla inesperada"));
    }

    // Este es un poco más complejo porque requiere mockear MethodArgumentNotValidException
    @Test
    void handleValidationErrors_returnsFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        var bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(
                java.util.List.of(new org.springframework.validation.FieldError("obj", "email", "Email inválido"))
        );

        ResponseEntity<?> response = exceptionManager.handleValidationErrors(ex);
        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Email inválido", body.get("email"));
    }
}