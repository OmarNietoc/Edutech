package com.edutech.support.service;

import com.edutech.support.dto.SoporteRequestDTO;
import com.edutech.support.dto.UsuarioDTO;
import com.edutech.support.exception.ResourceNotFoundException;
import com.edutech.support.model.Soporte;
import com.edutech.support.repository.SoporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupportServiceTest {

    @Mock
    private SoporteRepository soporteRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SoporteService soporteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerTodas_debeRetornarListaDeSoportes() {
        Soporte soporte = new Soporte();
        when(soporteRepository.findAll()).thenReturn(List.of(soporte));

        List<Soporte> result = soporteService.obtenerTodas();

        assertEquals(1, result.size());
        verify(soporteRepository).findAll();
    }

    @Test
    void obtenerSoportePorId_existente_retornaSoporte() {
        Soporte soporte = new Soporte();
        soporte.setId(1L);
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporte));

        Soporte result = soporteService.ObtenerSoportePorId(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void obtenerSoportePorId_noExistente_lanzaExcepcion() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> soporteService.ObtenerSoportePorId(1L));
    }

    @Test
    void crear_debeGuardarYRetornarSoporte() {
        SoporteRequestDTO dto = new SoporteRequestDTO();
        dto.setDescripcion("Prueba");
        dto.setUsuarioId(123L);
        dto.setPrioridad("Alta");

        Soporte soporteGuardado = new Soporte();
        soporteGuardado.setId(1L);
        soporteGuardado.setDescripcion("Prueba");

        when(soporteRepository.save(any())).thenReturn(soporteGuardado);

        Soporte result = soporteService.crear(dto);

        assertEquals("Prueba", result.getDescripcion());
        verify(soporteRepository).save(any());
    }

    @Test
    void eliminar_existente_debeEliminar() {
        Soporte soporte = new Soporte();
        soporte.setId(1L);

        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporte));

        soporteService.eliminar(1L);

        verify(soporteRepository).deleteById(1L);
    }

    @Test
    void actualizarEstado_estadoValido_actualizaCorrectamente() {
        Soporte soporte = new Soporte();
        soporte.setId(1L);
        soporte.setEstado("PENDIENTE");

        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporte));
        when(soporteRepository.save(any())).thenReturn(soporte);

        Soporte result = soporteService.actualizarEstado(1L, "RESUELTO");

        assertEquals("RESUELTO", result.getEstado());
    }

    @Test
    void actualizarEstado_estadoInvalido_lanzaExcepcion() {
        Soporte soporte = new Soporte();
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporte));

        assertThrows(IllegalArgumentException.class, () -> soporteService.actualizarEstado(1L, ""));
    }

    @Test
    void actualizarEstado_soporteNoExiste_lanzaExcepcion() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> soporteService.actualizarEstado(1L, "RESUELTO"));
    }

    @Test
    void obtenerDatosUsuario_debeRetornarUsuarioDTO() {
        UsuarioDTO mockUsuario = new UsuarioDTO();
        mockUsuario.setId(123L);
        mockUsuario.setNombre("Juan");

        when(restTemplate.getForObject(eq("http://localhost:8080/api/usuarios/123"), eq(UsuarioDTO.class)))
                .thenReturn(mockUsuario);

        UsuarioDTO result = soporteService.obtenerDatosUsuario(123L);

        assertEquals("Juan", result.getNombre());
    }
    @Test
    void actualizarEstado_conEstadoNull_debeLanzarExcepcion() {
        Long soporteId = 1L;
        Soporte soporte = new Soporte();
        soporte.setId(soporteId);

        when(soporteRepository.findById(soporteId)).thenReturn(Optional.of(soporte));

        assertThrows(IllegalArgumentException.class, () -> {
            soporteService.actualizarEstado(soporteId, null);
        });

        verify(soporteRepository, never()).save(any());
    }
    @Test
    void actualizarEstado_conEstadoVacio_debeLanzarExcepcion() {
        Long soporteId = 1L;
        Soporte soporte = new Soporte();
        soporte.setId(soporteId);

        when(soporteRepository.findById(soporteId)).thenReturn(Optional.of(soporte));

        assertThrows(IllegalArgumentException.class, () -> {
            soporteService.actualizarEstado(soporteId, "");
        });

        verify(soporteRepository, never()).save(any());
    }
}
