package com.edutech.courses.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @NotNull(message = "El monto de descuento es obligatorio.")
    @Column(nullable = false)
    @DecimalMin(value = "0.01", message = "El descuento debe ser mayor a 0")
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private boolean active;
}
