package com.edutech.payments.config;

import com.edutech.payments.model.Payment;
import com.edutech.payments.model.PaymentStatus;
import com.edutech.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PaymentService paymentService;

    @Override
    public void run(String... args) {
        // Cargar pagos de prueba
        List<Payment> payments = List.of(
                new Payment("TRX000021", 1L, new BigDecimal("49900"), LocalDateTime.now().minusDays(3), PaymentStatus.CONFIRMED),
                new Payment("TRX000022", 3L, new BigDecimal("59900"), LocalDateTime.now().minusDays(1), PaymentStatus.PENDING),
                new Payment("TRX000023", 2L, new BigDecimal("69900"), LocalDateTime.now(), PaymentStatus.REJECTED)
        );
        payments.forEach(paymentService::savePayment);

        System.out.println("✅ Datos de prueba cargados correctamente en PaymentService.");
    }
}
