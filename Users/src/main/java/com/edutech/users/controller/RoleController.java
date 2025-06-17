package com.edutech.users.controller;

import com.edutech.users.controller.response.MessageResponse;
import com.edutech.users.model.Role;
import com.edutech.users.model.User;
import com.edutech.users.repository.RoleRepository;
import com.edutech.users.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/edutech/users/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }


    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addRole(@Valid @RequestBody Role role) {
        roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Rol creado exitosamente."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateRole(@PathVariable Long id, @Valid @RequestBody Role roleDetails) {
        roleService.updateRole(id, roleDetails);
        return ResponseEntity.ok(new MessageResponse("Rol actualizado exitosamente."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRole(@PathVariable Long id) {
        roleService.getRoleById(id);
        roleService.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Rol eliminado exitosamente."));
    }
}

