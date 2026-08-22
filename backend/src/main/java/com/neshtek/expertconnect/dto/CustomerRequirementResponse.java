package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerRequirementResponse(
    Long id, String companyName, String contactName, String email, String phone, String country, String city,
    String title, String description, String technology, BigDecimal requiredExperienceYears, BigDecimal estimatedHours,
    LocalDate preferredStartDate, String priority, BigDecimal budget, String currencyCode, String status,
    LocalDateTime createdAt, LocalDateTime updatedAt, List<SkillResponse> skills
) {
    public record SkillResponse(Long id, String skillName, Integer priorityOrder) {}
}
