package com.edutech.courses.service;

import com.edutech.courses.controller.response.MessageResponse;
import com.edutech.courses.exception.ResourceNotFoundException;
import com.edutech.courses.model.Coupon;
import com.edutech.courses.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCoupons_conResultados() {
        when(couponRepository.findAll()).thenReturn(List.of(new Coupon()));
        var response = couponService.getAllCoupons();
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getAllCoupons_sinResultados() {
        when(couponRepository.findAll()).thenReturn(List.of());
        var response = couponService.getAllCoupons();
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void getCouponById_existente() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        assertEquals(coupon, couponService.getCouponById(1L));
    }

    @Test
    void getCouponById_inexistente_lanzaExcepcion() {
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> couponService.getCouponById(1L));
    }

    @Test
    void getCouponByCode_existente() {
        Coupon coupon = new Coupon();
        coupon.setCode("ABC123");
        when(couponRepository.findByCode("ABC123")).thenReturn(Optional.of(coupon));
        assertEquals(coupon, couponService.getCouponByCode("ABC123"));
    }

    @Test
    void getCouponByCode_inexistente_lanzaExcepcion() {
        when(couponRepository.findByCode("NOPE")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> couponService.getCouponByCode("NOPE"));
    }

    @Test
    void createCoupon_funciona() {
        BigDecimal descuento = BigDecimal.valueOf(15.5);
        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = couponService.createCoupon(descuento);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(descuento, response.getBody().getDiscountAmount());
        assertTrue(response.getBody().isActive());
        assertNotNull(response.getBody().getCode());
        assertEquals(9, response.getBody().getCode().length());
    }

    @Test
    void updateCouponById_funciona() {
        Coupon existente = new Coupon(1L, "OLD123", BigDecimal.TEN, true);
        Coupon actualizado = new Coupon(null, "NEW999", BigDecimal.valueOf(20), false);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(couponRepository.save(any(Coupon.class))).thenReturn(existente);

        var response = couponService.updateCouponById(1L, actualizado);

        assertEquals("Cupón actualizado correctamente.", response.getBody().getMessage());
        assertEquals("NEW999", existente.getCode());
        assertEquals(BigDecimal.valueOf(20), existente.getDiscountAmount());
        assertFalse(existente.isActive());
    }

    @Test
    void deleteCoupon_funciona() {
        Coupon existente = new Coupon();
        when(couponRepository.findById(1L)).thenReturn(Optional.of(existente));

        var response = couponService.deleteCoupon(1L);

        verify(couponRepository).delete(existente);
        assertEquals("Cupón eliminado correctamente.", response.getBody().getMessage());
    }

    @Test
    void updateCouponStatusByCode_funciona() {
        Coupon existente = new Coupon();
        existente.setCode("ABC123");
        existente.setActive(true);

        when(couponRepository.findByCode("ABC123")).thenReturn(Optional.of(existente));
        when(couponRepository.save(any(Coupon.class))).thenReturn(existente);

        var response = couponService.updateCouponStatusByCode("ABC123", false);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(((Coupon) response.getBody()).isActive());
    }
}
