package com.neshtek.expertconnect.dto;

import java.time.LocalDateTime;

public record EngagementHistoryResponse(
        Long id,
        String action,
        String fromStatus,
        String toStatus,
        Long actorUserId,
        String actorRole,
        String actorName,
        String reason,
        LocalDateTime occurredAt
) {}
