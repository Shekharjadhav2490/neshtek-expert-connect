package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.EngagementBillingSummaryResponse;
import com.neshtek.expertconnect.entity.Engagement;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.EngagementRepository;
import com.neshtek.expertconnect.repository.BillingWorkLogRepository;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class BillingSummaryService {
    private final EngagementRepository engagements;
    private final BillingWorkLogRepository workLogs;
    private final ResourceAuthorizationService authorization;

    public BillingSummaryService(EngagementRepository engagements, BillingWorkLogRepository workLogs, ResourceAuthorizationService authorization) {
        this.engagements = engagements;
        this.workLogs = workLogs;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public EngagementBillingSummaryResponse get(Long engagementId) {
        Engagement e = engagements.findWithDetailsById(engagementId)
                .orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + engagementId));
        authorization.assertCanAccess(e);
        BigDecimal contract = e.getConsultationRequest().getEstimatedHours();
        BigDecimal submitted = workLogs.sumSubmittedHours(engagementId);
        BigDecimal approved = workLogs.sumApprovedHours(engagementId);
        BigDecimal rejected = workLogs.sumRejectedHours(engagementId);
        BigDecimal logged = submitted.add(approved).add(rejected);
        BigDecimal remaining = contract == null ? null : contract.subtract(logged).max(BigDecimal.ZERO);
        BigDecimal utilization = contract == null || contract.signum() == 0 ? null : logged.multiply(BigDecimal.valueOf(100)).divide(contract, 2, RoundingMode.HALF_UP);
        BigDecimal rate = e.getConsultationRequest().getProposedRate();
        BigDecimal contractValue = money(contract, rate);
        BigDecimal approvedBilling = money(approved, rate);
        BigDecimal pendingBilling = money(submitted, rate);
        BigDecimal rejectedValue = money(rejected, rate);
        return new EngagementBillingSummaryResponse(e.getId(), e.getStatus().name(), e.getRequirement().getTitle(), contract, logged, submitted, approved, rejected, submitted, remaining, utilization, rate, e.getConsultationRequest().getCurrencyCode(), contractValue, approvedBilling, pendingBilling, rejectedValue);
    }

    private BigDecimal money(BigDecimal hours, BigDecimal rate) {
        return hours == null || rate == null ? null : hours.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
