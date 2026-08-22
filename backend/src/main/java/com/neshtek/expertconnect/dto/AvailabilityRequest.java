package com.neshtek.expertconnect.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AvailabilityRequest(
        @DecimalMin("0.0") BigDecimal hoursPerWeek,
        LocalDate availableFrom,
        String timezone,
        @Pattern(regexp = "[YN]") String weekdayAvailable,
        @Pattern(regexp = "[YN]") String weekendAvailable
) {}
