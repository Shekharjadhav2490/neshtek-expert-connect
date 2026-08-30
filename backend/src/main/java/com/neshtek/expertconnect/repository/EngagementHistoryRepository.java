package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.EngagementHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EngagementHistoryRepository extends JpaRepository<EngagementHistory, Long> {
    List<EngagementHistory> findByEngagementIdOrderByOccurredAtDesc(Long engagementId);
}
