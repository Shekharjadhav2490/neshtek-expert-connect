package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.WorkLogRequest;
import com.neshtek.expertconnect.dto.WorkLogResponse;
import com.neshtek.expertconnect.entity.Engagement;
import com.neshtek.expertconnect.entity.EngagementStatus;
import com.neshtek.expertconnect.entity.EngagementWorkLog;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.EngagementRepository;
import com.neshtek.expertconnect.repository.EngagementWorkLogRepository;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class EngagementWorkLogService {
    private final EngagementRepository engagementRepository;
    private final EngagementWorkLogRepository workLogRepository;
    private final ResourceAuthorizationService authorization;

    public EngagementWorkLogService(EngagementRepository engagementRepository,
                                     EngagementWorkLogRepository workLogRepository,
                                     ResourceAuthorizationService authorization) {
        this.engagementRepository = engagementRepository;
        this.workLogRepository = workLogRepository;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<WorkLogResponse> list(Long engagementId, Pageable pageable) {
        Engagement engagement = findEngagement(engagementId);
        authorization.assertCanAccess(engagement);
        return workLogRepository.findByEngagementId(engagementId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalHours(Long engagementId) {
        Engagement engagement = findEngagement(engagementId);
        authorization.assertCanAccess(engagement);
        return workLogRepository.totalHours(engagementId);
    }

    @Transactional
    public WorkLogResponse create(Long engagementId, WorkLogRequest request) {
        Engagement engagement = findEngagement(engagementId);
        authorization.assertExpertOwns(engagement.getExpert().getId());

        if (engagement.getStatus() != EngagementStatus.ACTIVE) {
            throw new IllegalArgumentException("Work can only be logged for an ACTIVE engagement");
        }
        if (request == null || request.workDate() == null || request.hours() == null || request.description() == null) {
            throw new IllegalArgumentException("Work date, hours and description are required");
        }
        if (request.workDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Work date cannot be in the future");
        }
        if (request.hours().compareTo(BigDecimal.ZERO) <= 0 || request.hours().compareTo(BigDecimal.valueOf(24)) > 0) {
            throw new IllegalArgumentException("Hours must be greater than 0 and no more than 24");
        }
        String description = request.description().trim();
        if (description.isEmpty() || description.length() > 2000) {
            throw new IllegalArgumentException("Description must contain 1 to 2000 characters");
        }

        EngagementWorkLog workLog = new EngagementWorkLog();
        workLog.setEngagement(engagement);
        workLog.setWorkDate(request.workDate());
        workLog.setHours(request.hours());
        workLog.setDescription(description);
        return toResponse(workLogRepository.save(workLog));
    }

    private Engagement findEngagement(Long id) {
        return engagementRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + id));
    }

    private WorkLogResponse toResponse(EngagementWorkLog w) {
        return new WorkLogResponse(w.getId(), w.getEngagement().getId(), w.getWorkDate(), w.getHours(),
                w.getDescription(), w.getCreatedAt(), w.getUpdatedAt());
    }
}
