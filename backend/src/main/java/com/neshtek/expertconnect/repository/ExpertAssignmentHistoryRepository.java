package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.ExpertAssignmentHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpertAssignmentHistoryRepository extends JpaRepository<ExpertAssignmentHistory, Long> {
    @EntityGraph(attributePaths={"expert","actor"})
    List<ExpertAssignmentHistory> findByEngagementIdOrderByEffectiveFromDesc(Long engagementId);
}
