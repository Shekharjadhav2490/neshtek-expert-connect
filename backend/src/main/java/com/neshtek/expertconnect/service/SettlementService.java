package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.EngagementBillingSummaryResponse;
import com.neshtek.expertconnect.dto.SettlementResponse;
import com.neshtek.expertconnect.entity.Engagement;
import com.neshtek.expertconnect.entity.Settlement;
import com.neshtek.expertconnect.entity.SettlementStatus;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.EngagementRepository;
import com.neshtek.expertconnect.repository.SettlementRepository;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SettlementService {
    private final SettlementRepository settlements;
    private final EngagementRepository engagements;
    private final BillingSummaryService billing;
    private final ResourceAuthorizationService authorization;

    public SettlementService(SettlementRepository settlements, EngagementRepository engagements,
                             BillingSummaryService billing, ResourceAuthorizationService authorization) {
        this.settlements = settlements;
        this.engagements = engagements;
        this.billing = billing;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<SettlementResponse> byExpert(Long expertId, Pageable pageable) {
        authorization.assertExpertOwns(expertId);
        return settlements.findByExpertIdOrderByCreatedAtDesc(expertId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<SettlementResponse> byCustomer(Long customerId, Pageable pageable) {
        authorization.assertCustomerOwns(customerId);
        return settlements.findByEngagementCustomerIdOrderByCreatedAtDesc(customerId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public SettlementResponse byEngagement(Long engagementId) {
        Engagement engagement = engagements.findWithDetailsById(engagementId)
                .orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + engagementId));
        authorization.assertCanAccess(engagement);
        return settlements.findFirstByEngagementIdOrderByCreatedAtDesc(engagementId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No settlement found for engagement: " + engagementId));
    }

    @Transactional(readOnly = true)
    public Page<SettlementResponse> all(Pageable pageable) {
        if (!authorization.isAdmin()) throw new AccessDeniedException("Admin access required");
        return settlements.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public SettlementResponse get(Long id) {
        Settlement s = find(id);
        authorization.assertCanAccess(s.getEngagement());
        return toResponse(s);
    }

    @Transactional
    public SettlementResponse request(Long engagementId) {
        Engagement e = engagements.findWithDetailsById(engagementId)
                .orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + engagementId));
        authorization.assertExpertOwns(e.getExpert().getId());

        EngagementBillingSummaryResponse summary = billing.get(engagementId);
        if (summary.pendingHours() != null && summary.pendingHours().signum() > 0)
            throw new IllegalArgumentException("Settlement can only be requested after all submitted work logs are approved");
        if (summary.approvedBilling() == null || summary.approvedBilling().signum() <= 0)
            throw new IllegalArgumentException("Settlement can only be requested when approved earnings are greater than zero");
        if (settlements.existsByEngagementIdAndStatusIn(engagementId,
                List.of(SettlementStatus.REQUESTED, SettlementStatus.CUSTOMER_APPROVED, SettlementStatus.APPROVED_FOR_PAYOUT)))
            throw new IllegalArgumentException("This engagement already has an open settlement request");

        Settlement s = new Settlement();
        s.setEngagement(e);
        s.setExpert(e.getExpert());
        s.setApprovedHours(summary.approvedHours());
        s.setHourlyRate(summary.hourlyRate());
        s.setGrossAmount(summary.approvedBilling());
        s.setCurrencyCode(summary.currencyCode());
        s.setStatus(SettlementStatus.REQUESTED);
        return toResponse(settlements.save(s));
    }

    @Transactional
    public SettlementResponse customerApprove(Long id, String comment) {
        Settlement s = find(id);
        authorization.assertCustomerOwns(s.getEngagement().getCustomer().getId());
        if (s.getStatus() != SettlementStatus.REQUESTED)
            throw new IllegalArgumentException("Only REQUESTED settlements can be approved by the customer");
        s.setStatus(SettlementStatus.CUSTOMER_APPROVED);
        s.setCustomerApprovedAt(LocalDateTime.now());
        s.setCustomerComment(blankToNull(comment));
        return toResponse(settlements.save(s));
    }

    @Transactional
    public SettlementResponse customerReject(Long id, String reason) {
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("A rejection reason is required");
        Settlement s = find(id);
        authorization.assertCustomerOwns(s.getEngagement().getCustomer().getId());
        if (s.getStatus() != SettlementStatus.REQUESTED)
            throw new IllegalArgumentException("Only REQUESTED settlements can be rejected by the customer");
        s.setStatus(SettlementStatus.REJECTED);
        s.setCustomerRejectedAt(LocalDateTime.now());
        s.setCustomerComment(reason.trim());
        return toResponse(settlements.save(s));
    }

    @Transactional
    public SettlementResponse approve(Long id, String comment) {
        requireAdmin();
        Settlement s = find(id);
        if (s.getStatus() != SettlementStatus.CUSTOMER_APPROVED)
            throw new IllegalArgumentException("Only CUSTOMER_APPROVED settlements can be approved for payout");
        s.setStatus(SettlementStatus.APPROVED_FOR_PAYOUT);
        s.setApprovedAt(LocalDateTime.now());
        s.setAdminComment(blankToNull(comment));
        return toResponse(settlements.save(s));
    }

    @Transactional
    public SettlementResponse reject(Long id, String reason) {
        requireAdmin();
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("A rejection reason is required");
        Settlement s = find(id);
        if (s.getStatus() != SettlementStatus.CUSTOMER_APPROVED && s.getStatus() != SettlementStatus.REQUESTED)
            throw new IllegalArgumentException("Only REQUESTED or CUSTOMER_APPROVED settlements can be rejected");
        s.setStatus(SettlementStatus.REJECTED);
        s.setRejectedAt(LocalDateTime.now());
        s.setAdminComment(reason.trim());
        return toResponse(settlements.save(s));
    }

    @Transactional
    public SettlementResponse markPaid(Long id, String paymentReference) {
        requireAdmin();
        if (paymentReference == null || paymentReference.isBlank())
            throw new IllegalArgumentException("Payment reference is required when marking a settlement as paid");
        Settlement s = find(id);
        if (s.getStatus() != SettlementStatus.APPROVED_FOR_PAYOUT)
            throw new IllegalArgumentException("Only APPROVED_FOR_PAYOUT settlements can be marked as paid");
        s.setStatus(SettlementStatus.PAID);
        s.setPaidAt(LocalDateTime.now());
        s.setPaymentReference(paymentReference.trim());
        return toResponse(settlements.save(s));
    }

    private void requireAdmin() {
        if (!authorization.isAdmin()) throw new AccessDeniedException("Admin access required");
    }

    private Settlement find(Long id) {
        return settlements.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement not found: " + id));
    }

    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }

    private SettlementResponse toResponse(Settlement s) {
        Engagement e = s.getEngagement();
        String expertName = s.getExpert().getFirstName() + " " + s.getExpert().getLastName();
        return new SettlementResponse(s.getId(), e.getId(), s.getExpert().getId(), expertName,
                e.getCustomer().getId(), e.getCustomer().getCompanyName(), e.getRequirement().getTitle(),
                s.getApprovedHours(), s.getHourlyRate(), s.getGrossAmount(), s.getCurrencyCode(),
                s.getStatus().name(), s.getRequestedAt(), s.getCustomerApprovedAt(), s.getCustomerRejectedAt(),
                s.getCustomerComment(), s.getApprovedAt(), s.getPaidAt(), s.getRejectedAt(),
                s.getAdminComment(), s.getPaymentReference());
    }
}
