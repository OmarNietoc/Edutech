package com.edutech.courses.service;

import com.edutech.courses.exception.ConflictException;
import com.edutech.courses.exception.ResourceNotFoundException;
import com.edutech.courses.model.Category;
import com.edutech.courses.repository.CategoryRepository;
import com.edutech.courses.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCategories_deberiaRetornarLista() {
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));
        assertEquals(1, categoryService.getAllCategories().size());
    }

    @Test
    void getCategoryById_existente() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        assertEquals(category, categoryService.getCategoryById(1L));
    }

    @Test
    void getCategoryById_inexistente_lanzaExcepcion() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void createCategory_funciona() {
        Category category = new Category();
        category.setName("Backend");

        when(categoryRepository.existsByNameIgnoreCase("Backend")).thenReturn(false);

        categoryService.createCategory(category);

        verify(categoryRepository).save(category);
    }

    @Test
    void createCategory_nombreDuplicado_lanzaExcepcion() {
        Category category = new Category();
        category.setName("Backend");

        when(categoryRepository.existsByNameIgnoreCase("Backend")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(category));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_funciona() {
        Category existente = new Category();
        existente.setId(1L);
        existente.setName("Antiguo");

        Category actualizado = new Category();
        actualizado.setName("Nuevo");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Nuevo", 1L)).thenReturn(false);

        categoryService.updateCategory(1L, actualizado);

        assertEquals("Nuevo", existente.getName());
        verify(categoryRepository).save(existente);
    }

    @Test
    void updateCategory_nombreDuplicado_lanzaConflict() {
        Category existente = new Category();
        existente.setId(1L);
        existente.setName("Antiguo");

        Category actualizado = new Category();
        actualizado.setName("Duplicado");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Duplicado", 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.updateCategory(1L, actualizado));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_noExiste_lanzaResourceNotFound() {
        Category actualizado = new Category();
        actualizado.setName("Nuevo");

        when(categoryRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(5L, actualizado));
    }

    @Test
    void deleteCategory_funciona() {
        Category cat = new Category();
        cat.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(courseRepository.existsCoursesByCategoryId(1L)).thenReturn(false);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_conCursosAsociados_lanzaIllegalState() {
        Category cat = new Category();
        cat.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(courseRepository.existsCoursesByCategoryId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void deleteCategory_noExiste_lanzaResourceNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(99L));
    }
}
