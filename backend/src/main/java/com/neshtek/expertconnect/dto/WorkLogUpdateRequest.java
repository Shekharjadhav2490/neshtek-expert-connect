package com.neshtek.expertconnect.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record WorkLogUpdateRequest(
        @NotNull LocalDate workDate,
        @NotNull @DecimalMin("0.01") @DecimalMax("24.00") BigDecimal hours,
        @NotBlank @Size(max = 2000) String description) {}
