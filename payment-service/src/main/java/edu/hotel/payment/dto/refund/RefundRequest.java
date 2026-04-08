package edu.hotel.payment.dto.refund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class RefundRequest {

    @NotNull(message = "Нужно число")
    @Positive
    private BigDecimal amount;

    @NotBlank(message = "Причина не может быть пустой")
    private String reason;
}
