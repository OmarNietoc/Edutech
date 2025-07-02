package com.edutech.courses.service;

import com.edutech.courses.client.UserClient;
import com.edutech.courses.controller.response.MessageResponse;
import com.edutech.courses.controller.response.UserResponseDto;
import com.edutech.courses.dto.CourseDto;
import com.edutech.courses.dto.RoleDto;
import com.edutech.courses.exception.ResourceNotFoundException;
import com.edutech.courses.model.Category;
import com.edutech.courses.model.Course;
import com.edutech.courses.model.Level;
import com.edutech.courses.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private LevelService levelService;
    @Mock
    private UserClient userClient;
    @Mock
    private UserValidatorService userValidatorService;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCourses_sinFiltros() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Course> page = new PageImpl<>(Collections.singletonList(new Course()));
        when(courseRepository.findAll(pageable)).thenReturn(page);
        Page<Course> result = courseService.getCourses(0, 10, null, null);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getCourses_conAmbosFiltros_retornaFiltradosPorCategoriaYNivel() {
        Page<Course> pageResult = new PageImpl<>(List.of(new Course()));
        when(courseRepository.findByCategoryIdAndLevelId(eq(1L), eq(2L), any(Pageable.class)))
                .thenReturn(pageResult);

        Page<Course> result = courseService.getCourses(0, 10, 1L, 2L);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getCourses_soloCategoria_retornaFiltradosPorCategoria() {
        Page<Course> pageResult = new PageImpl<>(List.of(new Course()));
        when(courseRepository.findByCategoryId(eq(1L), any(Pageable.class)))
                .thenReturn(pageResult);

        Page<Course> result = courseService.getCourses(0, 10, 1L, null);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getCourses_soloNivel_retornaFiltradosPorNivel() {
        Page<Course> pageResult = new PageImpl<>(List.of(new Course()));
        when(courseRepository.findByLevelId(eq(2L), any(Pageable.class)))
                .thenReturn(pageResult);

        Page<Course> result = courseService.getCourses(0, 10, null, 2L);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getCourses_sinFiltros_retornaTodosLosCursos() {
        Page<Course> pageResult = new PageImpl<>(List.of(new Course()));
        when(courseRepository.findAll(any(Pageable.class)))
                .thenReturn(pageResult);

        Page<Course> result = courseService.getCourses(0, 10, null, null);
        assertEquals(1, result.getContent().size());
    }


    @Test
    void getCourseById_existente() {
        Course course = new Course();
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        assertEquals(course, courseService.getCourseById(1L));
    }

    @Test
    void getCourseById_inexistente_lanzaExcepcion() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(1L));
    }

    @Test
    void createCourse_funciona() {
        CourseDto dto = new CourseDto();
        dto.setTitle("Curso Java");
        dto.setDescription("Intro a Java");
        dto.setCategoryId(1L);
        dto.setLevelId(2L);
        dto.setInstructorId(10L);
        dto.setPrice(BigDecimal.valueOf(99.9));
        dto.setTags(Arrays.asList("java", "spring", "backend"));

        Category category = new Category();
        Level level = new Level();

        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(levelService.getLevelById(2L)).thenReturn(level);
        when(userValidatorService.validateInstructor(anyLong())).thenReturn(
                new UserResponseDto(1L, "Nombre", "email@test.com", new RoleDto(1L, "INSTRUCTOR"), 1)
        );

        ResponseEntity<?> response = courseService.createCourse(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Curso creado exitosamente"));
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void updateCourse_funciona() {
        CourseDto dto = new CourseDto();
        dto.setTitle("Nuevo título");
        dto.setDescription("Nueva descripción");
        dto.setCategoryId(1L);
        dto.setLevelId(2L);
        dto.setInstructorId(5L);
        dto.setPrice(BigDecimal.valueOf(149.0));
        dto.setTags(Arrays.asList("seguridad", "vigilancia", "estrategia"));

        Course existente = new Course();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoryService.getCategoryById(1L)).thenReturn(new Category());
        when(levelService.getLevelById(2L)).thenReturn(new Level());

        ResponseEntity<MessageResponse> response = courseService.updateCourse(1L, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Curso actualizado correctamente.", response.getBody().getMessage());
        verify(courseRepository).save(existente);
    }

    @Test
    void deleteCourse_funciona() {
        Course course = new Course();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        ResponseEntity<MessageResponse> response = courseService.deleteCourse(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Curso eliminado correctamente.", response.getBody().getMessage());
        verify(courseRepository).delete(course);
    }
}
