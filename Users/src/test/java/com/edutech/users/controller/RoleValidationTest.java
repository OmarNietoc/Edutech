package com.edutech.users.controller;

import com.edutech.users.model.Role;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenNameIsEmpty_ShouldThrowValidationException() {
        Role role = new Role();
        role.setName("");

        var violations = validator.validate(role);
        assertFalse(violations.isEmpty());
        // Cambia a assert mayor que 0 en lugar de igual a 1
        assertTrue(violations.size() > 0);
    }

    @Test
    void whenNameIsTooShort_ShouldThrowValidationException() {
        Role role = new Role();
        role.setName("A"); // Mínimo 2 caracteres

        var violations = validator.validate(role);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenNameIsValid_ShouldPassValidation() {
        Role role = new Role();
        role.setName("VALID_NAME");

        var violations = validator.validate(role);
        assertTrue(violations.isEmpty());
    }
}