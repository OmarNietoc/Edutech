package com.edutech.payments.service;


import com.edutech.payments.client.CourseClient;
import com.edutech.payments.client.UserClient;
import com.edutech.payments.controller.response.MessageResponse;
import com.edutech.payments.dto.EnrollmentDto;
import com.edutech.payments.dto.PaymentDto;
import com.edutech.payments.exception.ResourceNotFoundException;
import com.edutech.payments.model.Payment;
import com.edutech.payments.model.PaymentStatus;
import com.edutech.payments.repository.PaymentRepository;
import feign.FeignException;
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

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private CourseClient courseClient;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllPayments_retornaLista() {
        when(paymentRepository.findAll()).thenReturn(List.of(new Payment()));
        assertEquals(1, paymentService.getAllPayments().size());
    }

    @Test
    void getPaymentById_existente() {
        Payment mock = new Payment();
        mock.setId(1L);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mock));
        assertEquals(mock, paymentService.getPaymentById(1L));
    }

    @Test
    void getPaymentById_inexistente_lanzaExcepcion() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById(1L));
    }

    @Test
    void createPayment_funciona() {
        PaymentDto dto = new PaymentDto();
        dto.setEnrollmentId(5L);

        EnrollmentDto enrollment = new EnrollmentDto();
        enrollment.setId(5L);
        enrollment.setFinalPrice(BigDecimal.valueOf(123.4));

        when(courseClient.getEnrollmentDtoById(5L)).thenReturn(enrollment);

        ResponseEntity<MessageResponse> response = paymentService.createPayment(dto);

        assertEquals("Pago creado exitosamente.", response.getBody().getMessage());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void updatePayment_funciona() {
        PaymentDto dto = new PaymentDto();
        dto.setEnrollmentId(3L);

        EnrollmentDto enrollment = new EnrollmentDto();
        enrollment.setId(3L);
        enrollment.setFinalPrice(BigDecimal.valueOf(88.0));

        Payment mock = new Payment();
        mock.setId(1L);

        when(courseClient.getEnrollmentDtoById(3L)).thenReturn(enrollment);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mock));

        ResponseEntity<MessageResponse> response = paymentService.updatePayment(1L, dto);

        assertEquals("Pago actualizado exitosamente.", response.getBody().getMessage());
        verify(paymentRepository).save(mock);
    }

    @Test
    void updatePaymentStatus_funciona() {
        Payment mock = new Payment();
        mock.setId(1L);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mock));

        ResponseEntity<MessageResponse> response = paymentService.updatePaymentStatus(1L, PaymentStatus.CONFIRMED);

        assertTrue(response.getBody().getMessage().contains("CONFIRMED"));
        verify(paymentRepository).save(mock);
    }

    @Test
    void deletePaymentById_funciona() {
        Payment mock = new Payment();
        mock.setId(1L);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mock));

        paymentService.deletePaymentById(1L);

        verify(paymentRepository).delete(mock);
    }

    @Test
    void savePayment_guarda() {
        Payment pago = new Payment();
        paymentService.savePayment(pago);
        verify(paymentRepository).save(pago);
    }

    @Test
    void validateUser_lanzaResourceNotFoundException() throws Exception {
        doThrow(FeignException.NotFound.class).when(userClient).getUserById(10L);

        var method = PaymentService.class.getDeclaredMethod("validateUser", Long.class);
        method.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> method.invoke(paymentService, 10L));
        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        assertEquals("Usuario no encontrado.", ex.getCause().getMessage());
    }

    @Test
    void validateCourse_lanzaResourceNotFoundException() throws Exception {
        doThrow(FeignException.NotFound.class).when(courseClient).getCourseById(22L);

        var method = PaymentService.class.getDeclaredMethod("validateCourse", Long.class);
        method.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> method.invoke(paymentService, 22L));
        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        assertEquals("Curso no encontrado", ex.getCause().getMessage());
    }



    @Test
    void validateEnrollment_lanzaResourceNotFoundException() throws Exception {
        doThrow(FeignException.NotFound.class).when(courseClient).getEnrollmentDtoById(30L);

        var method = PaymentService.class.getDeclaredMethod("validateEnrollment", Long.class);
        method.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> method.invoke(paymentService, 30L));
        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        assertEquals("Inscripción no encontrada", ex.getCause().getMessage());
    }

    @Test
    void validateUser_cubreTry() throws Exception {
        // Cuando userClient responde bien (no lanza excepción)
        when(userClient.getUserById(10L)).thenReturn(null);

        var method = PaymentService.class.getDeclaredMethod("validateUser", Long.class);
        method.setAccessible(true);

        // No debe lanzar excepción
        method.invoke(paymentService, 10L);
        verify(userClient).getUserById(10L);
    }

    @Test
    void validateCourse_cubreTry() throws Exception {
        when(courseClient.getCourseById(22L)).thenReturn(null);

        var method = PaymentService.class.getDeclaredMethod("validateCourse", Long.class);
        method.setAccessible(true);

        method.invoke(paymentService, 22L);
        verify(courseClient).getCourseById(22L);
    }

}
