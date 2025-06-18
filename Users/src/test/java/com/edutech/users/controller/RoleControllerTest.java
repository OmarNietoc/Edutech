package com.edutech.users.controller;

import com.edutech.users.controller.response.MessageResponse;
import com.edutech.users.model.Role;
import com.edutech.users.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    // Test para GET /edutech/users/roles
    @Test
    void getAllRoles_ShouldReturnAllRoles() {
        // Arrange
        Role adminRole = new Role(1L, "ADMIN");
        Role userRole = new Role(2L, "USER");
        when(roleService.getAllRoles()).thenReturn(Arrays.asList(adminRole, userRole));

        // Act
        ResponseEntity<List<Role>> response = roleController.getAllRoles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().anyMatch(r -> r.getName().equals("ADMIN")));
        verify(roleService, times(1)).getAllRoles();
    }

    // Test para GET /edutech/users/roles/{id}
    @Test
    void getRoleById_WhenRoleExists_ShouldReturnRole() {
        // Arrange
        Long roleId = 1L;
        Role mockRole = new Role(roleId, "ADMIN");
        when(roleService.getRoleById(roleId)).thenReturn(mockRole);

        // Act
        ResponseEntity<?> response = roleController.getUserById(roleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRole, response.getBody());
        assertEquals("ADMIN", ((Role) response.getBody()).getName());
    }

    // Test para POST /edutech/users/roles/add
    @Test
    void addRole_WithValidData_ShouldReturnCreated() {
        // Arrange
        Role newRole = new Role(null, "EDITOR");
        // Cambia doNothing por when si el método devuelve algo
        when(roleService.createRole(any(Role.class))).thenReturn(newRole); // o cualquier valor esperado

        // Act
        ResponseEntity<MessageResponse> response = roleController.addRole(newRole);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Rol creado exitosamente.", response.getBody().getMessage());
        verify(roleService, times(1)).createRole(any(Role.class));
    }

    // Test para PUT /edutech/users/roles/{id}
    @Test
    void updateRole_WithValidData_ShouldReturnOk() {
        // Arrange
        Long roleId = 1L;
        Role updatedRole = new Role(null, "UPDATED_ADMIN");
        doNothing().when(roleService).updateRole(eq(roleId), any(Role.class));

        // Act
        ResponseEntity<MessageResponse> response = roleController.updateRole(roleId, updatedRole);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rol actualizado exitosamente.", response.getBody().getMessage());
        verify(roleService, times(1)).updateRole(eq(roleId), any(Role.class));
    }

    // Test para DELETE /edutech/users/roles/{id}
    @Test
    void deleteRole_WhenRoleExists_ShouldReturnOk() {
        // Arrange
        Long roleId = 1L;
        Role mockRole = new Role(roleId, "ADMIN");
        when(roleService.getRoleById(roleId)).thenReturn(mockRole);
        doNothing().when(roleService).deleteById(roleId);

        // Act
        ResponseEntity<MessageResponse> response = roleController.deleteRole(roleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rol eliminado exitosamente.", response.getBody().getMessage());
        verify(roleService, times(1)).deleteById(roleId);
    }
}