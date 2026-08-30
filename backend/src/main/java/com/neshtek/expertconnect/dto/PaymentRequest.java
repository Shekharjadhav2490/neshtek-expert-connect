package com.neshtek.expertconnect.dto;

import com.neshtek.expertconnect.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(
 @NotBlank String paymentReference,
 @NotNull @DecimalMin("0.01") BigDecimal amount,
 @NotNull PaymentMethod paymentMethod,
 LocalDate paymentDate,
 String notes
) {}
