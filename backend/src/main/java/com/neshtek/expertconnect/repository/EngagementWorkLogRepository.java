package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.EngagementWorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface EngagementWorkLogRepository extends JpaRepository<EngagementWorkLog, Long> {
    @Query("select w from EngagementWorkLog w join fetch w.engagement e " +
           "where e.id = :engagementId order by w.workDate desc, w.id desc")
    Page<EngagementWorkLog> findByEngagementId(Long engagementId, Pageable pageable);

    @Query("select coalesce(sum(w.hours), 0) from EngagementWorkLog w where w.engagement.id = :engagementId")
    BigDecimal totalHours(Long engagementId);
}
