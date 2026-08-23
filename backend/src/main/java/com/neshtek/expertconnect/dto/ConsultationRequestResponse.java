package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConsultationRequestResponse(
        Long id, Long customerId, Long requirementId, Long expertId, String expertName, String requirementTitle,
        String message, LocalDate requestedStartDate, BigDecimal estimatedHours, BigDecimal proposedRate,
        String currencyCode, String status, String rejectionReason, LocalDateTime respondedAt,
        LocalDateTime createdAt, LocalDateTime updatedAt
) {}
