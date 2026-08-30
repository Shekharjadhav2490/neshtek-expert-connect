package com.neshtek.expertconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.neshtek.expertconnect.entity.ExpertReplacementReason;

public record ExpertReplacementRequestDto(
        @NotNull ExpertReplacementReason reasonCode,
        @NotBlank @Size(max=2000) String comments
) {}
