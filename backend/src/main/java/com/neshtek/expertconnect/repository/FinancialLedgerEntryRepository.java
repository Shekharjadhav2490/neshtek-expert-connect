package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.FinancialLedgerEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.util.Optional;

public interface FinancialLedgerEntryRepository extends JpaRepository<FinancialLedgerEntry, Long> {
    Optional<FinancialLedgerEntry> findByIdempotencyKey(String idempotencyKey);
    Page<FinancialLedgerEntry> findByCustomerIdOrderByOccurredAtDesc(Long customerId, Pageable pageable);
    Page<FinancialLedgerEntry> findByExpertIdOrderByOccurredAtDesc(Long expertId, Pageable pageable);
    Page<FinancialLedgerEntry> findByEngagementIdOrderByOccurredAtDesc(Long engagementId, Pageable pageable);
    Page<FinancialLedgerEntry> findAllByOrderByOccurredAtDesc(Pageable pageable);
    BigDecimal sumAmountByCustomerIdAndDirection(Long customerId, com.neshtek.expertconnect.entity.FinancialLedgerDirection direction);
    BigDecimal sumAmountByExpertIdAndEntryType(Long expertId, com.neshtek.expertconnect.entity.FinancialLedgerEntryType entryType);
}
