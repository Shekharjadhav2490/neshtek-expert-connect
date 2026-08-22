package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.util.List;

public record ExpertMatchResponse(
        Long expertId,
        String firstName,
        String lastName,
        String city,
        String timezone,
        BigDecimal totalExperienceYears,
        BigDecimal hourlyRate,
        String currencyCode,
        BigDecimal matchScore,
        int matchedSkills,
        int requiredSkills,
        List<String> matchedSkillNames,
        boolean experienceMatch,
        boolean availabilityMatch,
        boolean technologyMatch
) {}
