package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface BillingWorkLogRepository extends JpaRepository<WorkLog, Long> {
    @Query("select coalesce(sum(w.hours), 0) from WorkLog w where w.engagement.id = :engagementId and w.status = com.neshtek.expertconnect.entity.WorkLogStatus.SUBMITTED")
    BigDecimal sumSubmittedHours(Long engagementId);

    @Query("select coalesce(sum(w.hours), 0) from WorkLog w where w.engagement.id = :engagementId and w.status = com.neshtek.expertconnect.entity.WorkLogStatus.APPROVED")
    BigDecimal sumApprovedHours(Long engagementId);

    @Query("select coalesce(sum(w.hours), 0) from WorkLog w where w.engagement.id = :engagementId and w.status = com.neshtek.expertconnect.entity.WorkLogStatus.REJECTED")
    BigDecimal sumRejectedHours(Long engagementId);
}
