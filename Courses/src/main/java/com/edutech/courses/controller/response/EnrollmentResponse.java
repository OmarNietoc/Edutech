package com.edutech.courses.controller.response;

import com.edutech.courses.controller.response.CourseResponse;
import com.edutech.courses.model.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long userId;
    private CourseResponse course;
    private CouponResponse coupon;
    private BigDecimal finalPrice;
    private LocalDateTime enrollmentDate;
    private Boolean active;
}