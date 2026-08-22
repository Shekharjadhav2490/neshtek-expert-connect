package com.neshtek.expertconnect.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record ConsultingRequest(
        @DecimalMin("0.5") BigDecimal minimumEngagementHours,
        @DecimalMin("0.5") BigDecimal maximumEngagementHours,
        @DecimalMin("0.0") BigDecimal hourlyRate,
        String currencyCode
) {}
