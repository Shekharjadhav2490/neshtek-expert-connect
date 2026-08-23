package com.neshtek.expertconnect.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CustomerRequirementRequest(
    @NotBlank @Size(max=200) String companyName,
    @NotBlank @Size(max=150) String contactName,
    @NotBlank @Email @Size(max=320) String email,
    @Size(max=30) String phone,
    @Size(max=100) String country,
    @Size(max=100) String city,
    @NotBlank @Size(max=250) String title,
    @NotBlank String description,
    @Size(max=150) String technology,
    @DecimalMin("0") BigDecimal requiredExperienceYears,
    @DecimalMin("0.1") BigDecimal estimatedHours,
    LocalDate preferredStartDate,
    String priority,
    @DecimalMin("0") BigDecimal budget,
    @Pattern(regexp="[A-Za-z]{3}") String currencyCode,
    @Valid @Size(max=20) List<SkillRequest> skills
) {
    public record SkillRequest(
            @NotBlank @Size(max=150) String skillName,
            @Min(1) @Max(999) Integer priorityOrder,
            Boolean mandatory
    ) {}
}
