package com.edutech.courses.service;

import com.edutech.courses.client.UserClient;
import com.edutech.courses.controller.response.UserResponseDto;
import com.edutech.courses.dto.RoleDto;
import com.edutech.courses.exception.ResourceNotFoundException;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserValidatorServiceTest {

    private UserClient userClient;
    private UserValidatorService userValidatorService;

    @BeforeEach
    void setUp() {
        userClient = mock(UserClient.class);
        userValidatorService = new UserValidatorService(userClient);
    }

    @Test
    void getUserById_devuelveUsuario() {
        UserResponseDto mockUser = new UserResponseDto(1L, "Test", "test@email.com", new RoleDto(1L, "STUDENT"), 1);
        when(userClient.getUserById(1L)).thenReturn(mockUser);

        UserResponseDto result = userValidatorService.getUserById(1L);

        assertEquals("Test", result.getName());
        verify(userClient).getUserById(1L);
    }

    @Test
    void getUserById_usuarioNoExiste_lanzaExcepcion() {
        FeignException.NotFound notFound = new FeignException.NotFound(
                "Not found",
                Request.create(Request.HttpMethod.GET, "/users/1", Collections.emptyMap(), null, StandardCharsets.UTF_8),
                null, null
        );

        when(userClient.getUserById(1L)).thenThrow(notFound);

        assertThrows(ResourceNotFoundException.class, () -> userValidatorService.getUserById(1L));
    }

    @Test
    void getUserById_errorGeneralFeign_lanzaIllegalArgumentException() {
        FeignException.InternalServerError serverError = new FeignException.InternalServerError(
                "Internal error",
                Request.create(Request.HttpMethod.GET, "/users/1", Collections.emptyMap(), null, StandardCharsets.UTF_8),
                null, null
        );

        when(userClient.getUserById(1L)).thenThrow(serverError);

        assertThrows(IllegalArgumentException.class, () -> userValidatorService.getUserById(1L));
    }

    @Test
    void validateInstructor_funciona() {
        UserResponseDto instructor = new UserResponseDto(1L, "Juan", "correo", new RoleDto(2L, "INSTRUCTOR"), 1);
        when(userClient.getUserById(1L)).thenReturn(instructor);

        UserResponseDto result = userValidatorService.validateInstructor(1L);

        assertEquals("Juan", result.getName());
    }

    @Test
    void validateInstructor_usuarioConOtroRol_lanzaExcepcion() {
        UserResponseDto student = new UserResponseDto(1L, "Ana", "correo", new RoleDto(2L, "STUDENT"), 1);
        when(userClient.getUserById(1L)).thenReturn(student);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> userValidatorService.validateInstructor(1L));
        assertTrue(ex.getMessage().contains("no tiene el role de INSTRUCTOR"));
    }

    @Test
    void validateRoleOfUser_rolCorrecto_retornarTrue() {
        assertTrue(userValidatorService.validateRoleOfUser("INSTRUCTOR", "instructor"));
    }

    @Test
    void validateRoleOfUser_rolNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> userValidatorService.validateRoleOfUser(null, "INSTRUCTOR"));
    }

    @Test
    void validateRoleOfUser_rolIncorrecto_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> userValidatorService.validateRoleOfUser("STUDENT", "INSTRUCTOR"));
    }
}
