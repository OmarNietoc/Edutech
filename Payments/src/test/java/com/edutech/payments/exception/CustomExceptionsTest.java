package com.edutech.payments.exception;


import com.edutech.payments.exception.ResourceNotFoundException;
import com.edutech.payments.exception.ConflictException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomExceptionsTest {

    @Test
    void testConflictExceptionMessage() {
        ConflictException exception = new ConflictException("Conflicto detectado");
        assertEquals("Conflicto detectado", exception.getMessage());
    }

    @Test
    void testResourceNotFoundExceptionMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso no encontrado");
        assertEquals("Recurso no encontrado", exception.getMessage());
    }
}