package com.edutech.support.exception;

import com.edutech.support.controller.response.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
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

class ExceptionManagerTest {

    private ExceptionHandlerController exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ExceptionHandlerController();
    }

    @Test
    void handleValidationExceptions_debeRetornarErroresDeValidacion() {
        // Simular errores de validación
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("obj", "nombre", "El nombre es obligatorio");
        FieldError error2 = new FieldError("obj", "email", "Formato inválido");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("nombre"));
        assertTrue(response.getBody().containsKey("email"));
        assertEquals("El nombre es obligatorio", response.getBody().get("nombre"));
    }

    @Test
    void handleResourceNotFound_debeRetornar404ConMensaje() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Soporte no encontrado");

        ResponseEntity<?> response = exceptionHandler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);

        MessageResponse message = (MessageResponse) response.getBody();
        assertEquals("Soporte no encontrado", message.getMessage());
    }
}
