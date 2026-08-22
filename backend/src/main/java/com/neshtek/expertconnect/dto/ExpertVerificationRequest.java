package com.neshtek.expertconnect.dto;

import jakarta.validation.constraints.NotBlank;

public record ExpertVerificationRequest(
        @NotBlank String verifiedBy,
        boolean mobileVerified,
        boolean emailVerified,
        boolean linkedinVerified,
        boolean identityVerified,
        boolean experienceVerified) {}
