package com.neshtek.expertconnect.dto;

import com.neshtek.expertconnect.entity.FinancialLedgerEntry;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinancialLedgerEntryResponse(
        Long id, Long customerId, Long expertId, Long engagementId, Long invoiceId, Long paymentId,
        Long settlementId, Long replacementRequestId, Long parentEntryId, String entryType,
        String direction, BigDecimal amount, String currencyCode, String sourceType, String sourceId,
        String description, LocalDateTime occurredAt, LocalDateTime createdAt, String createdBy) {
    public static FinancialLedgerEntryResponse from(FinancialLedgerEntry e) {
        return new FinancialLedgerEntryResponse(e.getId(),
                e.getCustomer() == null ? null : e.getCustomer().getId(),
                e.getExpert() == null ? null : e.getExpert().getId(),
                e.getEngagement() == null ? null : e.getEngagement().getId(),
                e.getInvoice() == null ? null : e.getInvoice().getId(),
                e.getPayment() == null ? null : e.getPayment().getId(),
                e.getSettlement() == null ? null : e.getSettlement().getId(),
                e.getReplacementRequest() == null ? null : e.getReplacementRequest().getId(),
                e.getParentEntry() == null ? null : e.getParentEntry().getId(),
                e.getEntryType().name(), e.getDirection().name(), e.getAmount(), e.getCurrencyCode(),
                e.getSourceType(), e.getSourceId(), e.getDescription(), e.getOccurredAt(), e.getCreatedAt(), e.getCreatedBy());
    }
}
