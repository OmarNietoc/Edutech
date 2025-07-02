package com.edutech.users.exception;

import com.edutech.users.exception.ConflictException;
import com.edutech.users.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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