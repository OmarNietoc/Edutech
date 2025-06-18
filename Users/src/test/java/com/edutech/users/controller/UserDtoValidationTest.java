package com.edutech.users.controller;

import com.edutech.users.dto.UserDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenNameIsEmpty_ShouldThrowValidationException() {
        UserDto user = new UserDto();
        user.setName("");
        user.setEmail("test@edutech.com");
        user.setPassword("password");
        user.setRole(1L);
        user.setStatus(1);

        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        // Cambia a assert mayor que 0
        assertTrue(violations.size() > 0);
    }

    @Test
    void whenEmailIsInvalid_ShouldThrowValidationException() {
        UserDto user = new UserDto();
        user.setName("Test User");
        user.setEmail("invalid-email");
        user.setPassword("password");
        user.setRole(1L);
        user.setStatus(1);

        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPasswordIsTooShort_ShouldThrowValidationException() {
        UserDto user = new UserDto();
        user.setName("Test User");
        user.setEmail("test@edutech.com");
        user.setPassword("123");
        user.setRole(1L);
        user.setStatus(1);

        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenRoleIsNull_ShouldThrowValidationException() {
        UserDto user = new UserDto();
        user.setName("Test User");
        user.setEmail("test@edutech.com");
        user.setPassword("password");
        user.setRole(null);
        user.setStatus(1);

        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenStatusIsInvalid_ShouldThrowValidationException() {
        UserDto user = new UserDto();
        user.setName("Test User");
        user.setEmail("test@edutech.com");
        user.setPassword("password");
        user.setRole(1L);
        user.setStatus(2); // Valor inválido

        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}