package com.neshtek.expertconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultationRejectRequest(
        @NotBlank @Size(max = 1000) String reason
) {}
