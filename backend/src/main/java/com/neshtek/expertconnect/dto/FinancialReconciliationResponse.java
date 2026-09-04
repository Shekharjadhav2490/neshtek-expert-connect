package com.neshtek.expertconnect.dto;

import java.math.BigDecimal;

public record FinancialReconciliationResponse(
        Long customerId,
        Long expertId,
        BigDecimal customerDebits,
        BigDecimal customerCredits,
        BigDecimal customerNet,
        BigDecimal expertEarnings,
        BigDecimal expertPayouts,
        BigDecimal expertOutstanding,
        boolean balanced) {}
