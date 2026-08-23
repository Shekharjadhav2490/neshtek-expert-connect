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
        int mandatorySkillsMatched,
        int mandatorySkillsRequired,
        int optionalSkillsMatched,
        int optionalSkillsRequired,
        List<String> missingMandatorySkills,
        List<String> missingOptionalSkills,
        boolean mandatorySkillsSatisfied,
        boolean experienceMatch,
        boolean availabilityMatch,
        boolean technologyMatch,
        String matchLevel,
        String recommendation
) {}
