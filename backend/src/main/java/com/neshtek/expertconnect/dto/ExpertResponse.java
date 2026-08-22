package com.neshtek.expertconnect.dto;

import com.neshtek.expertconnect.entity.ExpertStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ExpertResponse(
        Long expertId,
        String firstName,
        String lastName,
        String email,
        String mobileNumber,
        ExpertStatus status,
        int skillCount,
        int expertiseWordCount,
        LocalDateTime createdAt
) {}
