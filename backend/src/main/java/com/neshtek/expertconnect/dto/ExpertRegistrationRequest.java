package com.neshtek.expertconnect.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpertRegistrationRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 320) String email,
        @NotBlank @Size(max = 30) String mobileNumber,
        @NotNull @Past LocalDate dateOfBirth,
        @Size(max = 100) String country,
        @Size(max = 100) String city,
        @Size(max = 100) String timezone,
        @Size(max = 500) String linkedinUrl,
        @DecimalMin("0.0") @DecimalMax("80.0") BigDecimal totalExperienceYears,
        @NotEmpty @Size(min = 5, max = 10) List<@Valid SkillRequest> skills,
        @NotBlank String technicalExpertise,
        @Valid AvailabilityRequest availability,
        @Valid ConsultingRequest consulting
) {}
