package com.edutech.courses.service;

import com.edutech.courses.controller.response.CouponResponse;
import com.edutech.courses.controller.response.CourseResponse;
import com.edutech.courses.controller.response.EnrollmentResponse;
import com.edutech.courses.controller.response.MessageResponse;
import com.edutech.courses.controller.response.UserResponseDto;
import com.edutech.courses.dto.EnrollmentDto;
import com.edutech.courses.dto.RoleDto;
import com.edutech.courses.exception.ResourceNotFoundException;
import com.edutech.courses.model.*;
import com.edutech.courses.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseService courseService;
    @Mock
    private CouponService couponService;
    @Mock
    private UserValidatorService userValidatorService;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEnrollmentDtoById_devuelveDTO() {
        Enrollment enrollment = Enrollment.builder()
                .id(1L)
                .userId(2L)
                .course(new Course())
                .finalPrice(BigDecimal.TEN)
                .enrollmentDate(LocalDateTime.now())
                .active(true)
                .build();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        ResponseEntity<EnrollmentResponse> response = enrollmentService.getEnrollmentDtoById(1L);

        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getEnrollmentDtoById_conCupon_devuelveDTOConCupon() {
        Coupon coupon = Coupon.builder()
                .id(99L)
                .code("SUMMER2025")
                .discountAmount(BigDecimal.valueOf(25))
                .active(true)
                .build();

        Enrollment enrollment = Enrollment.builder()
                .id(1L)
                .userId(2L)
                .course(new Course()) // Puede ser un Course vacío, no afecta el objetivo de este test
                .coupon(coupon)
                .finalPrice(BigDecimal.valueOf(75))
                .enrollmentDate(LocalDateTime.now())
                .active(true)
                .build();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        ResponseEntity<EnrollmentResponse> response = enrollmentService.getEnrollmentDtoById(1L);

        // Verificaciones clave para cubrir convertCouponToResponse
        assertNotNull(response.getBody().getCoupon());
        assertEquals("SUMMER2025", response.getBody().getCoupon().getCode());
        assertEquals(BigDecimal.valueOf(25), response.getBody().getCoupon().getDiscountAmount());
        assertTrue(response.getBody().getCoupon().isActive());
    }

    @Test
    void getEnrollmentById_inexistente_lanzaExcepcion() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> enrollmentService.getEnrollmentById(1L));
    }

    @Test
    void getAllEnrollments_devuelveLista() {
        when(enrollmentRepository.findAll()).thenReturn(List.of(
                Enrollment.builder()
                        .id(1L)
                        .userId(2L)
                        .course(new Course())
                        .finalPrice(BigDecimal.TEN)
                        .enrollmentDate(LocalDateTime.now())
                        .active(true)
                        .build()
        ));

        ResponseEntity<List<EnrollmentResponse>> response = enrollmentService.getAllEnrollments();
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createEnrollment_funcionaSinCupon() {
        EnrollmentDto dto = new EnrollmentDto();
        dto.setUserId(1L);
        dto.setCourseId(2L);
        dto.setCouponCode(null);

        Course course = Course.builder().price(BigDecimal.valueOf(100)).build();
        UserResponseDto user = new UserResponseDto(1L, "nombre", "email", new RoleDto(1L, "ADMIN"), 1);

        when(userValidatorService.getUserById(1L)).thenReturn(user);
        when(courseService.getCourseById(2L)).thenReturn(course);

        ResponseEntity<MessageResponse> response = enrollmentService.createEnrollment(dto);
        assertEquals("Inscripción creada exitosamente.", response.getBody().getMessage());
    }

    @Test
    void createEnrollment_funcionaConCupon() {
        EnrollmentDto dto = new EnrollmentDto();
        dto.setUserId(1L);
        dto.setCourseId(2L);
        dto.setCouponCode("ABC123");

        Course course = Course.builder().price(BigDecimal.valueOf(100)).build();
        UserResponseDto user = new UserResponseDto(1L, "nombre", "email", new RoleDto(1L, "ADMIN"), 1);
        Coupon coupon = Coupon.builder().code("ABC123").discountAmount(BigDecimal.valueOf(20)).active(true).build();

        when(userValidatorService.getUserById(1L)).thenReturn(user);
        when(courseService.getCourseById(2L)).thenReturn(course);
        when(couponService.getCouponByCode("ABC123")).thenReturn(coupon);

        ResponseEntity<MessageResponse> response = enrollmentService.createEnrollment(dto);
        assertEquals("Inscripción creada exitosamente.", response.getBody().getMessage());
        verify(couponService).updateCouponStatusByCode("ABC123", false);
    }

    @Test
    void createEnrollment_lanzaExcepcionSiCuponInactivo() {
        EnrollmentDto dto = new EnrollmentDto();
        dto.setUserId(1L);
        dto.setCourseId(2L);
        dto.setCouponCode("ABC123");

        Course course = Course.builder().price(BigDecimal.valueOf(100)).build();
        UserResponseDto user = new UserResponseDto(1L, "nombre", "email", new RoleDto(1L, "ADMIN"), 1);
        Coupon coupon = Coupon.builder().code("ABC123").discountAmount(BigDecimal.valueOf(20)).active(false).build();

        when(userValidatorService.getUserById(1L)).thenReturn(user);
        when(courseService.getCourseById(2L)).thenReturn(course);
        when(couponService.getCouponByCode("ABC123")).thenReturn(coupon);

        assertThrows(IllegalArgumentException.class, () -> enrollmentService.createEnrollment(dto));
    }

    @Test
    void updateEnrollmentStatusById_funciona() {
        Enrollment enrollment = Enrollment.builder().id(1L).active(false).build();
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        enrollmentService.updateEnrollmentStatusById(1L, true);
        assertTrue(enrollment.isActive());
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void updateEnrollmentStatusById_nulo_lanzaExcepcion() {
        Enrollment enrollment = Enrollment.builder().id(1L).active(false).build();
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        assertThrows(IllegalArgumentException.class, () -> enrollmentService.updateEnrollmentStatusById(1L, null));
    }

    @Test
    void deleteEnrollment_funciona() {
        Enrollment enrollment = Enrollment.builder().id(1L).build();
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        ResponseEntity<MessageResponse> response = enrollmentService.deleteEnrollment(1L);
        assertEquals("Inscripción eliminada correctamente.", response.getBody().getMessage());
        verify(enrollmentRepository).delete(enrollment);
    }

    @Test
    void updateEnrollment_funciona() {
        EnrollmentDto dto = new EnrollmentDto();
        dto.setUserId(1L);
        dto.setCourseId(2L);
        dto.setCouponCode(null);

        Enrollment existing = Enrollment.builder().id(1L).build();
        Course course = Course.builder().price(BigDecimal.valueOf(100)).build();
        UserResponseDto user = new UserResponseDto();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userValidatorService.getUserById(1L)).thenReturn(user);
        when(courseService.getCourseById(2L)).thenReturn(course);

        ResponseEntity<MessageResponse> response = enrollmentService.updateEnrollment(1L, dto);
        assertEquals("Inscripción actualizada exitosamente.", response.getBody().getMessage());
    }

    @Test
    void createEnrollment_cuponMayorQuePrecio_finalPriceCero() {
        // Arrange
        EnrollmentDto dto = new EnrollmentDto(1L, 2L, "BIGDISCOUNT");

        Course course = Course.builder()
                .price(BigDecimal.valueOf(30)) // Precio del curso
                .build();

        Coupon coupon = Coupon.builder()
                .code("BIGDISCOUNT")
                .discountAmount(BigDecimal.valueOf(100)) // Descuento mayor que el precio
                .active(true)
                .build();

        UserResponseDto user = new UserResponseDto(1L, "nombre", "email", new RoleDto(1L, "STUDENT"), 1);

        when(userValidatorService.getUserById(1L)).thenReturn(user);
        when(courseService.getCourseById(2L)).thenReturn(course);
        when(couponService.getCouponByCode("BIGDISCOUNT")).thenReturn(coupon);

        // Act
        ResponseEntity<MessageResponse> response = enrollmentService.createEnrollment(dto);

        // Assert
        assertEquals("Inscripción creada exitosamente.", response.getBody().getMessage());

        // También puedes verificar que el precio final quedó en 0
        verify(enrollmentRepository).save(argThat(enrollment ->
                BigDecimal.ZERO.compareTo(enrollment.getFinalPrice()) == 0
        ));

        // Verifica que se desactivó el cupón
        verify(couponService).updateCouponStatusByCode("BIGDISCOUNT", false);
    }

    @Test
    void getEnrollmentDtoById_conCursoValido_devuelveDTOConCurso() {
        Course course = Course.builder()
                .id(55L)
                .title("Curso Java")
                .description("Curso avanzado de Java")
                .price(BigDecimal.valueOf(199.99))
                .build();

        Enrollment enrollment = Enrollment.builder()
                .id(1L)
                .userId(2L)
                .course(course)
                .finalPrice(BigDecimal.valueOf(199.99))
                .enrollmentDate(LocalDateTime.now())
                .active(true)
                .build();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        ResponseEntity<EnrollmentResponse> response = enrollmentService.getEnrollmentDtoById(1L);

        CourseResponse courseResp = response.getBody().getCourse();
        assertNotNull(courseResp);
        assertEquals(55L, courseResp.getId());
        assertEquals("Curso Java", courseResp.getTitle());
    }

    @Test
    void deleteEnrollment_noExiste_lanzaExcepcion() {
        Long enrollmentId = 999L;

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> enrollmentService.deleteEnrollment(enrollmentId));
    }

    @Test
    void createEnrollment_codigoCuponVacio_ignoraCupon() {
        EnrollmentDto dto = new EnrollmentDto();
        dto.setUserId(1L);
        dto.setCourseId(2L);
        dto.setCouponCode("");

        Course course = Course.builder().price(BigDecimal.valueOf(100)).build();
        UserResponseDto user = new UserResponseDto(1L, "nombre", "email", new RoleDto(1L, "ADMIN"), 1);

        when(userValidatorService.getUserById(1L)).thenReturn(user);
        when(courseService.getCourseById(2L)).thenReturn(course);

        ResponseEntity<MessageResponse> response = enrollmentService.createEnrollment(dto);

        assertEquals("Inscripción creada exitosamente.", response.getBody().getMessage());

        verify(couponService, never()).getCouponByCode(anyString());
    }

}
