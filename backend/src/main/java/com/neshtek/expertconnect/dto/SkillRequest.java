package com.neshtek.expertconnect.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record SkillRequest(
        @NotBlank @Size(max = 150) String skillName,
        @Size(max = 30) String skillLevel,
        @DecimalMin("0.0") @DecimalMax("80.0") BigDecimal yearsExperience
) {}
