package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;

public record BillingSummary(Long engagementId, BigDecimal contractHours, BigDecimal loggedHours, BigDecimal approvedHours, BigDecimal pendingHours, BigDecimal rejectedHours, BigDecimal remainingHours, BigDecimal hourlyRate, String currencyCode, BigDecimal contractValue, BigDecimal approvedBilling, BigDecimal pendingBilling) {}
