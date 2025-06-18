package com.edutech.users.controller;

import com.edutech.users.controller.response.MessageResponse;
import com.edutech.users.dto.UserDto;
import com.edutech.users.model.Role;
import com.edutech.users.model.User;
import com.edutech.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    // Test para GET /edutech/users
    @Test
    void getUsers_ShouldReturnAllUsers() {
        // Arrange
        Role adminRole = new Role(1L, "ADMIN");
        User user1 = new User(1L, "Juan Pérez", "juan@edutech.com", "password123", adminRole, 1);
        User user2 = new User(2L, "María García", "maria@edutech.com", "password456", adminRole, 1);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // Act
        ResponseEntity<List<User>> response = userController.getUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    // Test para GET /edutech/users/{id}
    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        Long userId = 1L;
        Role userRole = new Role(2L, "USER");
        User mockUser = new User(userId, "Ana López", "ana@edutech.com", "password789", userRole, 1);

        when(userService.getUserById(userId)).thenReturn(mockUser);

        // Act
        ResponseEntity<?> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    // Test para POST /edutech/users/add
    @Test
    void addUser_WithValidData_ShouldReturnCreated() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setName("Carlos Sánchez");
        userDto.setEmail("carlos@edutech.com");
        userDto.setPassword("Password123");
        userDto.setRole(1L); // ID de rol existente
        userDto.setStatus(1);

        doNothing().when(userService).createUser(any(UserDto.class));

        // Act
        ResponseEntity<MessageResponse> response = userController.addUser(userDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Usuario agregado exitosamente.", response.getBody().getMessage());
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    // Test para PUT /edutech/users/{id}
    @Test
    void updateUser_WithValidData_ShouldReturnOk() {
        // Arrange
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Carlos Sánchez Actualizado");
        userDto.setEmail("carlos.updated@edutech.com");
        userDto.setPassword("NewPassword123");
        userDto.setRole(2L);
        userDto.setStatus(0);

        doNothing().when(userService).updateUser(userId, userDto);

        // Act
        ResponseEntity<MessageResponse> response = userController.updateUser(userId, userDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario actualizado exitosamente.", response.getBody().getMessage());
    }

    // Test para DELETE /edutech/users/{id}
    @Test
    void deleteUser_WhenUserExists_ShouldReturnOk() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userService).deleteUserById(userId);

        // Act
        ResponseEntity<MessageResponse> response = userController.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario eliminado correctamente.", response.getBody().getMessage());
        verify(userService, times(1)).deleteUserById(userId);
    }
}