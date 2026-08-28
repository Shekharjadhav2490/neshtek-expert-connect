package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.EngagementUsageResponse;
import com.neshtek.expertconnect.dto.WorkLogCreateRequest;
import com.neshtek.expertconnect.dto.WorkLogResponse;
import com.neshtek.expertconnect.dto.WorkLogUpdateRequest;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class WorkLogService {
    private final WorkLogRepository repository;
    private final EngagementRepository engagementRepository;
    private final ResourceAuthorizationService authorization;

    public WorkLogService(WorkLogRepository repository, EngagementRepository engagementRepository, ResourceAuthorizationService authorization) {
        this.repository = repository;
        this.engagementRepository = engagementRepository;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<WorkLogResponse> byEngagement(Long id, Pageable p) {
        Engagement e = engagementRepository.findWithDetailsById(id).orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + id));
        authorization.assertCanAccess(e);
        return repository.findByEngagementIdOrderByWorkDateDescIdDesc(id, p).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<WorkLogResponse> byCustomer(Long id, Pageable p) {
        authorization.assertCustomerOwns(id);
        return repository.findByEngagementCustomerIdOrderByWorkDateDescIdDesc(id, p).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<WorkLogResponse> byExpert(Long id, Pageable p) {
        authorization.assertExpertOwns(id);
        return repository.findByEngagementExpertIdOrderByWorkDateDescIdDesc(id, p).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<WorkLogResponse> all(Pageable p) {
        if (!authorization.isAdmin()) throw new org.springframework.security.access.AccessDeniedException("Admin access required");
        return repository.findAllByOrderByWorkDateDescIdDesc(p).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public EngagementUsageResponse usage(Long id) {
        Engagement e = findEngagement(id);
        authorization.assertCanAccess(e);
        return toUsage(e);
    }

    @Transactional
    public WorkLogResponse create(Long id, WorkLogCreateRequest r) {
        Engagement e = findEngagement(id);
        authorization.assertExpertOwns(e.getExpert().getId());
        if (e.getStatus() != EngagementStatus.ACTIVE) throw new IllegalArgumentException("Work can only be logged for ACTIVE engagements");
        validateDate(r.workDate());
        ensureWithinHours(e, r.hours(), null);
        WorkLog l = new WorkLog();
        l.setEngagement(e); l.setWorkDate(r.workDate()); l.setHours(r.hours()); l.setDescription(r.description().trim());
        return toResponse(repository.save(l));
    }

    @Transactional
    public WorkLogResponse update(Long id, WorkLogUpdateRequest r) {
        WorkLog l = find(id);
        authorization.assertExpertOwns(l.getEngagement().getExpert().getId());
        if (l.getStatus() != WorkLogStatus.DRAFT && l.getStatus() != WorkLogStatus.REJECTED) throw new IllegalArgumentException("Only DRAFT or REJECTED work logs can be edited");
        validateDate(r.workDate());
        ensureWithinHours(l.getEngagement(), r.hours(), l.getHours());
        l.setWorkDate(r.workDate()); l.setHours(r.hours()); l.setDescription(r.description().trim());
        return toResponse(repository.save(l));
    }

    @Transactional
    public WorkLogResponse submit(Long id) {
        WorkLog l = find(id);
        authorization.assertExpertOwns(l.getEngagement().getExpert().getId());
        if (l.getStatus() != WorkLogStatus.DRAFT && l.getStatus() != WorkLogStatus.REJECTED) throw new IllegalArgumentException("Only DRAFT or REJECTED work logs can be submitted");
        l.setStatus(WorkLogStatus.SUBMITTED); l.setSubmittedAt(LocalDateTime.now()); l.setReviewedAt(null); l.setReviewerComment(null);
        return toResponse(repository.save(l));
    }

    @Transactional
    public WorkLogResponse approve(Long id, String c) {
        WorkLog l = find(id);
        authorization.assertCustomerOwns(l.getEngagement().getCustomer().getId());
        if (l.getStatus() != WorkLogStatus.SUBMITTED) throw new IllegalArgumentException("Only SUBMITTED work logs can be approved");
        l.setStatus(WorkLogStatus.APPROVED); l.setReviewedAt(LocalDateTime.now()); l.setReviewerComment(c);
        return toResponse(repository.save(l));
    }

    @Transactional
    public WorkLogResponse reject(Long id, String c) {
        WorkLog l = find(id);
        authorization.assertCustomerOwns(l.getEngagement().getCustomer().getId());
        if (l.getStatus() != WorkLogStatus.SUBMITTED) throw new IllegalArgumentException("Only SUBMITTED work logs can be rejected");
        l.setStatus(WorkLogStatus.REJECTED); l.setReviewedAt(LocalDateTime.now()); l.setReviewerComment(c);
        return toResponse(repository.save(l));
    }

    private Engagement findEngagement(Long id) {
        return engagementRepository.findWithDetailsById(id).orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + id));
    }

    private void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Work date cannot be in the future");
    }

    private void ensureWithinHours(Engagement e, BigDecimal requestedHours, BigDecimal previousHours) {
        BigDecimal limit = e.getConsultationRequest().getEstimatedHours();
        if (limit == null || limit.signum() <= 0) return;
        BigDecimal current = repository.sumHoursByEngagementId(e.getId());
        BigDecimal effectiveCurrent = current.subtract(previousHours == null ? BigDecimal.ZERO : previousHours);
        if (effectiveCurrent.add(requestedHours).compareTo(limit) > 0) {
            BigDecimal remaining = limit.subtract(effectiveCurrent).max(BigDecimal.ZERO).stripTrailingZeros();
            throw new IllegalArgumentException("Work log exceeds the engagement hour limit. Remaining hours: " + remaining.toPlainString());
        }
    }

    private EngagementUsageResponse toUsage(Engagement e) {
        BigDecimal estimated = e.getConsultationRequest().getEstimatedHours();
        BigDecimal logged = repository.sumHoursByEngagementId(e.getId());
        BigDecimal approved = repository.sumApprovedHoursByEngagementId(e.getId());
        BigDecimal remaining = estimated == null ? null : estimated.subtract(logged).max(BigDecimal.ZERO);
        BigDecimal utilization = estimated == null || estimated.signum() == 0 ? null : logged.multiply(BigDecimal.valueOf(100)).divide(estimated, 2, RoundingMode.HALF_UP);
        BigDecimal rate = e.getConsultationRequest().getProposedRate();
        BigDecimal estimatedAmount = estimated != null && rate != null ? estimated.multiply(rate).setScale(2, RoundingMode.HALF_UP) : null;
        BigDecimal approvedAmount = rate != null ? approved.multiply(rate).setScale(2, RoundingMode.HALF_UP) : null;
        return new EngagementUsageResponse(e.getId(), e.getStatus().name(), e.getRequirement().getTitle(), estimated, logged, approved, remaining, utilization, rate, e.getConsultationRequest().getCurrencyCode(), estimatedAmount, approvedAmount, e.getConsultationRequest().getRequestedStartDate());
    }

    private WorkLog find(Long id) { return repository.findWithDetailsById(id).orElseThrow(() -> new ResourceNotFoundException("Work log not found: " + id)); }

    private WorkLogResponse toResponse(WorkLog e) {
        Engagement g = e.getEngagement(); String n = g.getExpert().getFirstName() + " " + g.getExpert().getLastName();
        return new WorkLogResponse(e.getId(), g.getId(), g.getCustomer().getId(), g.getExpert().getId(), n, g.getRequirement().getId(), g.getRequirement().getTitle(), e.getWorkDate(), e.getHours(), e.getDescription(), e.getStatus().name(), e.getSubmittedAt(), e.getReviewedAt(), e.getReviewerComment(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
