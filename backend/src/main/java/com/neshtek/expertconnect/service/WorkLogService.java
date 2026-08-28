package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.WorkLogCreateRequest;
import com.neshtek.expertconnect.dto.WorkLogResponse;
import com.neshtek.expertconnect.entity.Engagement;
import com.neshtek.expertconnect.entity.EngagementStatus;
import com.neshtek.expertconnect.entity.WorkLog;
import com.neshtek.expertconnect.entity.WorkLogStatus;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.EngagementRepository;
import com.neshtek.expertconnect.repository.WorkLogRepository;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WorkLogService {
    private final WorkLogRepository repository;
    private final EngagementRepository engagementRepository;
    private final ResourceAuthorizationService authorization;

    public WorkLogService(WorkLogRepository repository, EngagementRepository engagementRepository, ResourceAuthorizationService authorization) {
        this.repository = repository; this.engagementRepository = engagementRepository; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<WorkLogResponse> byEngagement(Long engagementId, Pageable pageable) {
        Engagement e = engagementRepository.findWithDetailsById(engagementId).orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + engagementId));
        authorization.assertCanAccess(e);
        return repository.findByEngagementIdOrderByWorkDateDescIdDesc(engagementId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<WorkLogResponse> byCustomer(Long customerId, Pageable pageable) {
        authorization.assertCustomerOwns(customerId);
        return repository.findByEngagementCustomerIdOrderByWorkDateDescIdDesc(customerId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<WorkLogResponse> byExpert(Long expertId, Pageable pageable) {
        authorization.assertExpertOwns(expertId);
        return repository.findByEngagementExpertIdOrderByWorkDateDescIdDesc(expertId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<WorkLogResponse> all(Pageable pageable) {
        if (!authorization.isAdmin()) throw new org.springframework.security.access.AccessDeniedException("Admin access required");
        return repository.findAllByOrderByWorkDateDescIdDesc(pageable).map(this::toResponse);
    }

    @Transactional
    public WorkLogResponse create(Long engagementId, WorkLogCreateRequest request) {
        Engagement e = engagementRepository.findWithDetailsById(engagementId).orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + engagementId));
        authorization.assertExpertOwns(e.getExpert().getId());
        if (e.getStatus() != EngagementStatus.ACTIVE) throw new IllegalArgumentException("Work can only be logged for ACTIVE engagements");
        WorkLog log = new WorkLog(); log.setEngagement(e); log.setWorkDate(request.workDate()); log.setHours(request.hours()); log.setDescription(request.description());
        return toResponse(repository.save(log));
    }

    @Transactional
    public WorkLogResponse submit(Long id) {
        WorkLog log = find(id); authorization.assertExpertOwns(log.getEngagement().getExpert().getId());
        if (log.getStatus() != WorkLogStatus.DRAFT && log.getStatus() != WorkLogStatus.REJECTED) throw new IllegalArgumentException("Only DRAFT or REJECTED work logs can be submitted");
        log.setStatus(WorkLogStatus.SUBMITTED); log.setSubmittedAt(LocalDateTime.now()); log.setReviewedAt(null); log.setReviewerComment(null);
        return toResponse(repository.save(log));
    }

    @Transactional
    public WorkLogResponse approve(Long id, String comment) {
        WorkLog log = find(id); authorization.assertCustomerOwns(log.getEngagement().getCustomer().getId());
        if (log.getStatus() != WorkLogStatus.SUBMITTED) throw new IllegalArgumentException("Only SUBMITTED work logs can be approved");
        log.setStatus(WorkLogStatus.APPROVED); log.setReviewedAt(LocalDateTime.now()); log.setReviewerComment(comment);
        return toResponse(repository.save(log));
    }

    @Transactional
    public WorkLogResponse reject(Long id, String comment) {
        WorkLog log = find(id); authorization.assertCustomerOwns(log.getEngagement().getCustomer().getId());
        if (log.getStatus() != WorkLogStatus.SUBMITTED) throw new IllegalArgumentException("Only SUBMITTED work logs can be rejected");
        log.setStatus(WorkLogStatus.REJECTED); log.setReviewedAt(LocalDateTime.now()); log.setReviewerComment(comment);
        return toResponse(repository.save(log));
    }

    private WorkLog find(Long id) { return repository.findWithDetailsById(id).orElseThrow(() -> new ResourceNotFoundException("Work log not found: " + id)); }

    private WorkLogResponse toResponse(WorkLog e) {
        Engagement g=e.getEngagement(); String expertName=g.getExpert().getFirstName()+" "+g.getExpert().getLastName();
        return new WorkLogResponse(e.getId(),g.getId(),g.getCustomer().getId(),g.getExpert().getId(),expertName,g.getRequirement().getId(),g.getRequirement().getTitle(),e.getWorkDate(),e.getHours(),e.getDescription(),e.getStatus().name(),e.getSubmittedAt(),e.getReviewedAt(),e.getReviewerComment(),e.getCreatedAt(),e.getUpdatedAt());
    }
}
