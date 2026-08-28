package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EngagementUsageResponse(
        Long engagementId,
        String status,
        String requirementTitle,
        BigDecimal estimatedHours,
        BigDecimal loggedHours,
        BigDecimal approvedHours,
        BigDecimal remainingHours,
        BigDecimal utilizationPercentage,
        BigDecimal proposedRate,
        String currencyCode,
        BigDecimal estimatedAmount,
        BigDecimal approvedAmount,
        LocalDate requestedStartDate
) {}
