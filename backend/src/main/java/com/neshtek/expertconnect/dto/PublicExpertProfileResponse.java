package com.neshtek.expertconnect.dto;

import java.util.List;

public record PublicExpertProfileResponse(
        Long expertId,
        String firstName,
        String lastName,
        String city,
        String country,
        String timezone,
        Integer totalExperienceYears,
        String technicalExpertise,
        List<String> skills,
        String status,
        Boolean available,
        Integer hoursPerWeek,
        String currencyCode,
        java.math.BigDecimal hourlyRate
) {}
