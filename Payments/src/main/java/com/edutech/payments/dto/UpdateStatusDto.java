package com.edutech.payments.dto;

import com.edutech.payments.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDto {

    @NotNull(message = "El estado es obligatorio.")
    private PaymentStatus status;
}
