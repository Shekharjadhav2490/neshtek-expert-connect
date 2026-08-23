package com.neshtek.expertconnect.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ConsultationRequestRequest(
        Long customerId,
        Long requirementId,
        Long expertId,
        @Size(max = 2000) String message,
        LocalDate requestedStartDate,
        @DecimalMin(value = "0.1", inclusive = true) BigDecimal estimatedHours,
        @DecimalMin(value = "0", inclusive = true) BigDecimal proposedRate,
        @Pattern(regexp = "[A-Za-z]{3}") String currencyCode
) {}
