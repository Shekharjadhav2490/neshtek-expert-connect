package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.ExpertReplacementRequest;
import com.neshtek.expertconnect.entity.ExpertReplacementStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ExpertReplacementRequestRepository extends JpaRepository<ExpertReplacementRequest, Long> {
    @EntityGraph(attributePaths={"engagement","engagement.customer","engagement.expert","engagement.requirement","currentExpert","newExpert","requestedBy","reviewedBy"})
    Optional<ExpertReplacementRequest> findWithDetailsById(Long id);
    @EntityGraph(attributePaths={"engagement","engagement.customer","engagement.expert","engagement.requirement","currentExpert","newExpert","requestedBy","reviewedBy"})
    List<ExpertReplacementRequest> findByEngagementIdOrderByRequestedAtDesc(Long engagementId);
    @EntityGraph(attributePaths={"engagement","engagement.customer","engagement.expert","engagement.requirement","currentExpert","newExpert","requestedBy","reviewedBy"})
    List<ExpertReplacementRequest> findByStatusOrderByRequestedAtDesc(ExpertReplacementStatus status);
    boolean existsByEngagementIdAndStatus(Long engagementId, ExpertReplacementStatus status);
}
