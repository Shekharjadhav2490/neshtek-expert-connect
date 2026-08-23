package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.util.List;

public record PublicExpertSearchResponse(
        Long expertId,
        String firstName,
        String lastName,
        String city,
        String country,
        String timezone,
        BigDecimal totalExperienceYears,
        List<String> skills,
        Boolean available,
        BigDecimal hoursPerWeek,
        String currencyCode,
        BigDecimal hourlyRate
) {}
