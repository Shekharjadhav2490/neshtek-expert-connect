package com.neshtek.expertconnect.dto;

import jakarta.validation.constraints.NotBlank;

public record ExpertRejectRequest(@NotBlank(message = "Rejection reason is required") String reason) {}
