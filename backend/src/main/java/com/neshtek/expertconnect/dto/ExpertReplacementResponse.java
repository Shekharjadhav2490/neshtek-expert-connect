package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpertReplacementResponse(
        Long id, Long engagementId, Long requirementId, String requirementTitle,
        Long expertId, String expertName, String status, String reasonCode, String comments,
        LocalDateTime requestedAt, LocalDateTime workCutoffAt, LocalDateTime reviewedAt,
        String reviewerComment, BigDecimal approvedHours, BigDecimal eligibleAmount,
        BigDecimal paidAmount, BigDecimal balanceDue, BigDecimal refundOrCreditDue, String currencyCode
) {}
