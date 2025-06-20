package com.edutech.support.Controller;

import com.edutech.support.DTO.EstadoDTO;
import com.edutech.support.DTO.SoporteRequestDTO;
import com.edutech.support.Model.Soporte;
import com.edutech.support.Service.SoporteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/edutech/soporte")
public class SoporteController {

    @Autowired
    private SoporteService soporteService;

    @PostMapping
    public ResponseEntity<Soporte> crear(@Valid @RequestBody SoporteRequestDTO dto) {

        return ResponseEntity.status(201).body(soporteService.crear(dto));
    }

    @GetMapping
    public List<Soporte> obtenerTodas() {
        return soporteService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(soporteService.ObtenerSoportePorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        soporteService.eliminar(id);
        return ResponseEntity.ok("Soporte eliminado");
    }

    // SOLO EL ENCARGADO puede modificar el estado
    @PatchMapping("/{id}")
    public Soporte actualizarEstado(@PathVariable Long id, @Valid @RequestBody EstadoDTO dto) {
        return soporteService.actualizarEstado(id, dto.getEstado());
    }

}
