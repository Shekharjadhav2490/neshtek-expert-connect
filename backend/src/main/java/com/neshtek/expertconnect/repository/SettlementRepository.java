package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.Settlement;
import com.neshtek.expertconnect.entity.SettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement", "expert"})
    Page<Settlement> findByExpertIdOrderByCreatedAtDesc(Long expertId, Pageable pageable);

    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement", "expert"})
    Page<Settlement> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement", "expert"})
    Optional<Settlement> findWithDetailsById(Long id);

    Optional<Settlement> findFirstByEngagementIdOrderByCreatedAtDesc(Long engagementId);

    boolean existsByEngagementIdAndStatusIn(Long engagementId, java.util.Collection<SettlementStatus> statuses);
}
