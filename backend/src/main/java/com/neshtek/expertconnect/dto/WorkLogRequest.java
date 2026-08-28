package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WorkLogRequest(LocalDate workDate, BigDecimal hours, String description) {}
