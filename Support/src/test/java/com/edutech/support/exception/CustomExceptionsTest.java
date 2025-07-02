package com.edutech.support.exception;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomExceptionsTest {

    @Test
    void testResourceNotFoundExceptionMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso no encontrado");
        assertEquals("Recurso no encontrado", exception.getMessage());
    }
}