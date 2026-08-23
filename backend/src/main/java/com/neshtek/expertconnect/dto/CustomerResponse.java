package com.neshtek.expertconnect.dto;

import com.neshtek.expertconnect.entity.CustomerStatus;
import java.time.LocalDateTime;

public record CustomerResponse(
        Long customerId,
        String companyName,
        String contactName,
        String email,
        String phone,
        String country,
        String city,
        String timezone,
        String industry,
        String companySize,
        CustomerStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
