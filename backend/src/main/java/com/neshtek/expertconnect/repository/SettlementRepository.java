package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.Settlement;
import com.neshtek.expertconnect.entity.SettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement", "expert"})
    Page<Settlement> findByExpertIdOrderByCreatedAtDesc(Long expertId, Pageable pageable);
    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement", "expert"})
    Page<Settlement> findByEngagementCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement", "expert"})
    Page<Settlement> findAllByOrderByCreatedAtDesc(Pageable pageable);
    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement", "expert"})
    Optional<Settlement> findWithDetailsById(Long id);
    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement", "expert"})
    Optional<Settlement> findFirstByEngagementIdOrderByCreatedAtDesc(Long engagementId);
    boolean existsByEngagementIdAndStatusIn(Long engagementId, java.util.Collection<SettlementStatus> statuses);
    @Query("select coalesce(sum(s.grossAmount), 0) from Settlement s where s.engagement.id = :engagementId and s.status = com.neshtek.expertconnect.entity.SettlementStatus.PAID")
    BigDecimal sumPaidAmountByEngagementId(Long engagementId);
}
