package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.WorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement"})
    Optional<WorkLog> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement"})
    Page<WorkLog> findByEngagementIdOrderByWorkDateDescIdDesc(Long engagementId, Pageable pageable);

    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement"})
    Page<WorkLog> findByEngagementCustomerIdOrderByWorkDateDescIdDesc(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement"})
    Page<WorkLog> findByEngagementExpertIdOrderByWorkDateDescIdDesc(Long expertId, Pageable pageable);

    @EntityGraph(attributePaths = {"engagement", "engagement.customer", "engagement.expert", "engagement.requirement"})
    Page<WorkLog> findAllByOrderByWorkDateDescIdDesc(Pageable pageable);

    @Query("select coalesce(sum(w.hours), 0) from WorkLog w where w.engagement.id = :engagementId")
    BigDecimal sumHoursByEngagementId(Long engagementId);

    @Query("select coalesce(sum(w.hours), 0) from WorkLog w where w.engagement.id = :engagementId and w.status = com.neshtek.expertconnect.entity.WorkLogStatus.APPROVED")
    BigDecimal sumApprovedHoursByEngagementId(Long engagementId);
}
