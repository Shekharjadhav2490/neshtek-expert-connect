package com.neshtek.expertconnect.dto;

import com.neshtek.expertconnect.entity.FinancialLedgerDirection;
import com.neshtek.expertconnect.entity.FinancialLedgerEntryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FinancialLedgerAdjustmentRequest(
        @NotNull Long customerId,
        Long expertId,
        Long engagementId,
        Long replacementRequestId,
        Long parentEntryId,
        @NotNull FinancialLedgerEntryType entryType,
        @NotNull FinancialLedgerDirection direction,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String currencyCode,
        @NotBlank String sourceType,
        @NotBlank String sourceId,
        @NotBlank String description) {}
