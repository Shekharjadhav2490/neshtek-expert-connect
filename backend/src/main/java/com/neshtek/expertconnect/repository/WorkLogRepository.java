package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.WorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
