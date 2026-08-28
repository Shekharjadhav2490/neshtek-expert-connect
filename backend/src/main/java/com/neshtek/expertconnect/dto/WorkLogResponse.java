package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkLogResponse(
        Long id, Long engagementId, Long customerId, Long expertId, String expertName,
        Long requirementId, String requirementTitle, LocalDate workDate, BigDecimal hours,
        String description, String status, LocalDateTime submittedAt, LocalDateTime reviewedAt,
        String reviewerComment, LocalDateTime createdAt, LocalDateTime updatedAt) {}
