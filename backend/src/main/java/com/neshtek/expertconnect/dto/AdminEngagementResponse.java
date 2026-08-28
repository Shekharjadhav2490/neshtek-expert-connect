package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminEngagementResponse(
        Long id,
        Long consultationRequestId,
        Long customerId,
        String companyName,
        Long expertId,
        String expertName,
        Long requirementId,
        String requirementTitle,
        String status,
        LocalDate requestedStartDate,
        BigDecimal estimatedHours,
        BigDecimal agreedRate,
        String currencyCode,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        LocalDateTime cancelledAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
