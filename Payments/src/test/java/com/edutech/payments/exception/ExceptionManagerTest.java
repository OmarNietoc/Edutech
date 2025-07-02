package com.edutech.payments.exception;

import com.edutech.payments.controller.response.MessageResponse;
import com.edutech.payments.model.PaymentStatus;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExceptionManagerTest {

    private final ExceptionManager exceptionManager = new ExceptionManager();

    @Test
    void handleValidationErrors_retornaErroresDeValidacion() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("object", "campo1", "mensaje1");
        FieldError error2 = new FieldError("object", "campo2", "mensaje2");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<?> response = exceptionManager.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof java.util.Map);
    }

    @Test
    void handleInvalidEnumValue_enumInvalido() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "REJECTE", PaymentStatus.class, "status", null, null);

        ResponseEntity<MessageResponse> response = exceptionManager.handleInvalidEnumValue(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Solo se aceptan"));
    }

    @Test
    void handleHttpMessageNotReadable_enumInvalido() {
        // Creamos una instancia real de InvalidFormatException con un enum como tipo objetivo
        InvalidFormatException cause = new InvalidFormatException(
                null,
                "valor-invalido",
                "REJECTE",
                PaymentStatus.class
        );

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("mensaje", cause);

        ResponseEntity<MessageResponse> response = exceptionManager.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Solo se aceptan"));
    }

    @Test
    void handleHttpMessageNotReadable_causaNoEsInvalidFormatException() {
        // Caso donde el "cause" no es InvalidFormatException
        Throwable otraCausa = new RuntimeException("Causa distinta");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("mensaje", otraCausa);

        ResponseEntity<MessageResponse> response = exceptionManager.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Error en el formato"));
    }

    @Test
    void handleHttpMessageNotReadable_conFormatoNoEnum() {
        // Creamos una subclase anónima para controlar el getTargetType()
        InvalidFormatException fakeCause = new InvalidFormatException(
                null,
                "valor inválido",
                "value",
                String.class // String NO es enum
        ) {
            @Override
            public Class<?> getTargetType() {
                return String.class; // Aseguramos que no sea enum
            }
        };

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("mensaje", fakeCause);

        ResponseEntity<MessageResponse> response = exceptionManager.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Error en el formato"));
    }

    @Test
    void handleResourceNotFound_devuelve404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("No encontrado");

        ResponseEntity<?> response = exceptionManager.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(((MessageResponse) response.getBody()).getMessage().contains("No encontrado"));
    }

    @Test
    void handleInvalidEnumValue_tipoNoEnum() {
        // PaymentStatus no es requerido, pasamos String para no ser enum
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "someValue", String.class, "param", null, null
        );

        ResponseEntity<MessageResponse> response = exceptionManager.handleInvalidEnumValue(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Error en el parámetro"));
    }

    @Test
    void handleIllegalArgument_devuelve400() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");

        ResponseEntity<MessageResponse> response = exceptionManager.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Argumento inválido", response.getBody().getMessage());
    }

    @Test
    void handleConflict_devuelve409() {
        ConflictException ex = new ConflictException("Conflicto detectado");

        ResponseEntity<?> response = exceptionManager.handleConflict(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflicto detectado", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleConstraintViolation_devuelve400() {
        ConstraintViolationException ex = new ConstraintViolationException("Violación de restricción", null);

        ResponseEntity<?> response = exceptionManager.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Violación de restricción", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleException_errorGeneral() {
        Exception ex = new Exception("Algo falló");

        ResponseEntity<?> response = exceptionManager.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(((MessageResponse) response.getBody()).getMessage().contains("Algo falló"));
    }
}
