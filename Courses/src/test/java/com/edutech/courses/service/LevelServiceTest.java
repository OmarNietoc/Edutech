package com.edutech.courses.service;

import com.edutech.courses.exception.ConflictException;
import com.edutech.courses.exception.ResourceNotFoundException;
import com.edutech.courses.model.Level;
import com.edutech.courses.repository.CourseRepository;
import com.edutech.courses.repository.LevelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LevelServiceTest {

    private LevelRepository levelRepository;
    private CourseRepository courseRepository;
    private LevelService levelService;

    @BeforeEach
    void setUp() {
        levelRepository = mock(LevelRepository.class);
        courseRepository = mock(CourseRepository.class);
        levelService = new LevelService(levelRepository, courseRepository);
    }

    @Test
    void getLevelById_devuelveLevel() {
        Level level = new Level(1L, "Básico");
        when(levelRepository.findById(1L)).thenReturn(Optional.of(level));

        Level result = levelService.getLevelById(1L);
        assertEquals("Básico", result.getName());
    }

    @Test
    void getLevelById_noExiste_lanzaExcepcion() {
        when(levelRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> levelService.getLevelById(1L));
    }

    @Test
    void getLevelByName_devuelveLevel() {
        Level level = new Level(1L, "Avanzado");
        when(levelRepository.findByNameIgnoreCase("Avanzado")).thenReturn(Optional.of(level));

        Level result = levelService.getLevelByName("Avanzado");
        assertEquals("Avanzado", result.getName());
    }

    @Test
    void getLevelByName_noExiste_lanzaExcepcion() {
        when(levelRepository.findByNameIgnoreCase("Fake")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> levelService.getLevelByName("Fake"));
    }

    @Test
    void getAllLevels_devuelveLista() {
        when(levelRepository.findAll()).thenReturn(List.of(new Level(1L, "Medio")));
        assertEquals(1, levelService.getAllLevels().size());
    }

    @Test
    void validateLevelByName_disponible_retornaTrue() {
        when(levelRepository.findByNameIgnoreCase("Nuevo")).thenReturn(Optional.empty());
        assertTrue(levelService.validateLevelByName("Nuevo"));
    }

    @Test
    void validateLevelByName_yaExiste_lanzaExcepcion() {
        when(levelRepository.findByNameIgnoreCase("Básico")).thenReturn(Optional.of(new Level()));
        assertThrows(IllegalArgumentException.class, () -> levelService.validateLevelByName("Básico"));
    }

    @Test
    void createLevel_funciona() {
        Level level = new Level(null, "Nuevo");
        when(levelRepository.findByNameIgnoreCase("Nuevo")).thenReturn(Optional.empty());

        levelService.createLevel(level);

        verify(levelRepository).save(level);
    }

    @Test
    void createLevel_yaExiste_lanzaExcepcion() {
        Level level = new Level(null, "Básico");
        when(levelRepository.findByNameIgnoreCase("Básico")).thenReturn(Optional.of(new Level()));

        assertThrows(IllegalArgumentException.class, () -> levelService.createLevel(level));
    }

    @Test
    void createLevel_excepcionBaseDatos_lanzaExcepcion() {
        Level level = new Level(null, "Duplicado");

        when(levelRepository.findByNameIgnoreCase("Duplicado")).thenReturn(Optional.empty());
        doThrow(new DataIntegrityViolationException("")).when(levelRepository).save(level);

        assertThrows(IllegalArgumentException.class, () -> levelService.createLevel(level));
    }

    @Test
    void updateLevel_funciona() {
        Level existing = new Level(1L, "Antiguo");
        Level updated = new Level(null, "Nuevo");

        when(levelRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(levelRepository.findByNameIgnoreCase("Nuevo")).thenReturn(Optional.empty());

        levelService.updateLevel(1L, updated);

        assertEquals("Nuevo", existing.getName());
        verify(levelRepository).save(existing);
    }

    @Test
    void updateLevel_noExiste_lanzaExcepcion() {
        when(levelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> levelService.updateLevel(999L, new Level()));
    }

    @Test
    void updateLevel_nombreYaExiste_lanzaExcepcion() {
        Level existing = new Level(1L, "Original");
        Level conflict = new Level(null, "YaExiste");

        when(levelRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(levelRepository.findByNameIgnoreCase("YaExiste")).thenReturn(Optional.of(new Level()));

        assertThrows(IllegalArgumentException.class, () -> levelService.updateLevel(1L, conflict));
    }

    @Test
    void deleteLevel_funciona() {
        when(levelRepository.findById(1L)).thenReturn(Optional.of(new Level()));
        when(courseRepository.existsCoursesByLevelId(1L)).thenReturn(false);

        levelService.deleteLevel(1L);

        verify(levelRepository).deleteById(1L);
    }

    @Test
    void deleteLevel_conCursos_lanzaExcepcion() {
        when(levelRepository.findById(1L)).thenReturn(Optional.of(new Level()));
        when(courseRepository.existsCoursesByLevelId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> levelService.deleteLevel(1L));
    }

    @Test
    void createLevel_ifValidEjecutaSave() {
        Level level = new Level(null, "Valido");
        when(levelRepository.findByNameIgnoreCase("Valido")).thenReturn(Optional.empty());

        levelService.createLevel(level);

        // Captura y verifica que se llamó a save si y solo si el if fue true
        verify(levelRepository).save(level);
    }


}
