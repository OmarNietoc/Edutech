package com.edutech.courses.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentDto {

    @NotNull(message = "El userId no puede ser nulo")
    private Long userId;

    @NotNull(message = "El courseId no puede ser nulo")
    private Long courseId;

    // Código de cupón opcional
    private String couponCode;


}
