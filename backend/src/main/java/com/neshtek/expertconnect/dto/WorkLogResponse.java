package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkLogResponse(
        Long id,
        Long engagementId,
        LocalDate workDate,
        BigDecimal hours,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
