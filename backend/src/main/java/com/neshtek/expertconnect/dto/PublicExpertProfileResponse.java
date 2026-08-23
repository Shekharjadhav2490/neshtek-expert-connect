package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.util.List;

public record PublicExpertProfileResponse(
        Long expertId,
        String firstName,
        String lastName,
        String city,
        String country,
        String timezone,
        BigDecimal totalExperienceYears,
        String technicalExpertise,
        List<String> skills,
        String status,
        Boolean available,
        BigDecimal hoursPerWeek,
        String currencyCode,
        BigDecimal hourlyRate
) {}
