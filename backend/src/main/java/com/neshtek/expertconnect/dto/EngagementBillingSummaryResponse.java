package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;

public record EngagementBillingSummaryResponse(
        Long engagementId,
        String status,
        String requirementTitle,
        BigDecimal contractHours,
        BigDecimal loggedHours,
        BigDecimal submittedHours,
        BigDecimal approvedHours,
        BigDecimal rejectedHours,
        BigDecimal pendingHours,
        BigDecimal remainingHours,
        BigDecimal utilizationPercentage,
        BigDecimal hourlyRate,
        String currencyCode,
        BigDecimal contractValue,
        BigDecimal approvedBilling,
        BigDecimal pendingBilling,
        BigDecimal rejectedValue
) {}
