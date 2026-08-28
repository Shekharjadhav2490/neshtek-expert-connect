package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SettlementResponse(
        Long id, Long engagementId, Long expertId, String expertName,
        Long customerId, String companyName, String requirementTitle,
        BigDecimal approvedHours, BigDecimal hourlyRate, BigDecimal grossAmount,
        String currencyCode, String status, LocalDateTime requestedAt,
        LocalDateTime approvedAt, LocalDateTime paidAt, LocalDateTime rejectedAt,
        String adminComment, String paymentReference) {}
