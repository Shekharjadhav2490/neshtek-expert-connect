package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.FinancialLedgerDirection;
import com.neshtek.expertconnect.entity.FinancialLedgerEntry;
import com.neshtek.expertconnect.entity.FinancialLedgerEntryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.Optional;

public interface FinancialLedgerEntryRepository extends JpaRepository<FinancialLedgerEntry, Long> {
    Optional<FinancialLedgerEntry> findByIdempotencyKey(String idempotencyKey);
    Page<FinancialLedgerEntry> findByCustomerIdOrderByOccurredAtDesc(Long customerId, Pageable pageable);
    Page<FinancialLedgerEntry> findByExpertIdOrderByOccurredAtDesc(Long expertId, Pageable pageable);
    Page<FinancialLedgerEntry> findByEngagementIdOrderByOccurredAtDesc(Long engagementId, Pageable pageable);
    Page<FinancialLedgerEntry> findAllByOrderByOccurredAtDesc(Pageable pageable);

    @Query("select coalesce(sum(e.amount), 0) from FinancialLedgerEntry e where e.customer.id = :customerId and e.direction = :direction")
    BigDecimal sumCustomerAmount(@Param("customerId") Long customerId, @Param("direction") FinancialLedgerDirection direction);

    @Query("select coalesce(sum(e.amount), 0) from FinancialLedgerEntry e where e.expert.id = :expertId and e.entryType = :entryType")
    BigDecimal sumExpertAmount(@Param("expertId") Long expertId, @Param("entryType") FinancialLedgerEntryType entryType);
}
