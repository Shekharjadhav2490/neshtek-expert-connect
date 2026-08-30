package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.EngagementBillingSummaryResponse;
import com.neshtek.expertconnect.dto.ExpertMatchResponse;
import com.neshtek.expertconnect.dto.ExpertReplacementRequestDto;
import com.neshtek.expertconnect.dto.ExpertReplacementResponse;
import com.neshtek.expertconnect.entity.*;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.*;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ExpertReplacementService {
    private final ExpertReplacementRequestRepository requests;
    private final EngagementRepository engagements;
    private final ExpertAssignmentHistoryRepository assignmentHistory;
    private final SettlementRepository settlements;
    private final WorkLogRepository workLogs;
    private final BillingSummaryService billing;
    private final ResourceAuthorizationService authorization;
    private final ExpertRepository experts;
    private final ConsultationRequestRepository consultationRequests;
    private final EngagementService engagementService;
    private final ExpertMatchingService matchingService;
    private final JdbcTemplate jdbc;

    public ExpertReplacementService(ExpertReplacementRequestRepository requests, EngagementRepository engagements,
                                    ExpertAssignmentHistoryRepository assignmentHistory, SettlementRepository settlements,
                                    WorkLogRepository workLogs, BillingSummaryService billing,
                                    ResourceAuthorizationService authorization, ExpertRepository experts,
                                    ConsultationRequestRepository consultationRequests, EngagementService engagementService,
                                    ExpertMatchingService matchingService, JdbcTemplate jdbc) {
        this.requests=requests; this.engagements=engagements; this.assignmentHistory=assignmentHistory;
        this.settlements=settlements; this.workLogs=workLogs; this.billing=billing; this.authorization=authorization;
        this.experts=experts; this.consultationRequests=consultationRequests; this.engagementService=engagementService;
        this.matchingService=matchingService; this.jdbc=jdbc;
    }

    @Transactional
    public ExpertReplacementResponse request(Long engagementId, ExpertReplacementRequestDto dto) {
        Engagement e=findEngagement(engagementId);
        authorization.assertCustomerOwns(e.getCustomer().getId());
        if (e.getStatus()!=EngagementStatus.READY && e.getStatus()!=EngagementStatus.ACTIVE && e.getStatus()!=EngagementStatus.PAUSED)
            throw new IllegalArgumentException("Expert replacement can only be requested for READY, ACTIVE or PAUSED engagements");
        if (requests.existsByEngagementIdAndStatus(engagementId, ExpertReplacementStatus.REQUESTED))
            throw new IllegalArgumentException("A replacement request is already pending for this engagement");
        AppUser actor=authorization.currentUser();
        ExpertReplacementRequest r=new ExpertReplacementRequest();
        r.setEngagement(e); r.setCurrentExpert(e.getExpert()); r.setRequestedBy(actor);
        r.setReasonCode(dto.reasonCode()); r.setComments(dto.comments().trim()); r.setStatus(ExpertReplacementStatus.REQUESTED);
        r.setWorkCutoffAt(LocalDateTime.now());
        ExpertReplacementRequest saved=requests.save(r);
        refreshReconciliation(saved);
        ExpertAssignmentHistory h=new ExpertAssignmentHistory();
        h.setEngagement(e); h.setExpert(e.getExpert()); h.setAction("REPLACEMENT_REQUESTED"); h.setEffectiveFrom(saved.getRequestedAt());
        h.setReason(dto.reasonCode().name()+": "+dto.comments().trim()); h.setActor(actor); assignmentHistory.save(h);
        return toResponse(saved);
    }

    @Transactional(readOnly=true)
    public List<ExpertReplacementResponse> byEngagement(Long engagementId) {
        Engagement e=findEngagement(engagementId); authorization.assertCanAccess(e);
        return requests.findByEngagementIdOrderByRequestedAtDesc(engagementId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly=true)
    public List<ExpertReplacementResponse> pending() {
        requireAdmin();
        List<ExpertReplacementResponse> result=new ArrayList<>();
        result.addAll(requests.findByStatusOrderByRequestedAtDesc(ExpertReplacementStatus.REQUESTED).stream().map(this::toResponse).toList());
        result.addAll(requests.findByStatusOrderByRequestedAtDesc(ExpertReplacementStatus.APPROVED).stream().map(this::toResponse).toList());
        result.sort(Comparator.comparing(ExpertReplacementResponse::requestedAt).reversed());
        return result;
    }

    @Transactional(readOnly=true)
    public List<ExpertMatchResponse> matches(Long id, int limit) {
        requireAdmin();
        ExpertReplacementRequest r=find(id);
        if (r.getStatus()!=ExpertReplacementStatus.APPROVED)
            throw new IllegalArgumentException("Expert matches are available only after the replacement request is approved");
        return matchingService.findMatches(r.getEngagement().getRequirement().getId(), Math.min(Math.max(limit,1),20)).stream()
                .filter(m -> !m.expertId().equals(r.getCurrentExpert().getId())).toList();
    }

    @Transactional
    public ExpertReplacementResponse approve(Long id, String comment) {
        requireAdmin(); ExpertReplacementRequest r=find(id);
        if(r.getStatus()!=ExpertReplacementStatus.REQUESTED) throw new IllegalArgumentException("Only REQUESTED replacement requests can be approved");
        refreshReconciliation(r);
        r.setStatus(ExpertReplacementStatus.APPROVED); r.setReviewedBy(authorization.currentUser()); r.setReviewedAt(LocalDateTime.now());
        r.setReviewerComment(blankToNull(comment));
        return toResponse(requests.save(r));
    }

    @Transactional
    public ExpertReplacementResponse resolveFinancial(Long id, String action, BigDecimal amount, String note) {
        requireAdmin();
        ExpertReplacementRequest r=find(id);
        if (r.getStatus()!=ExpertReplacementStatus.APPROVED)
            throw new IllegalArgumentException("Financial treatment can only be resolved after replacement approval");
        String normalized=action==null?"":action.trim().toUpperCase();
        List<String> allowed=List.of("NO_ADJUSTMENT","REFUND_CUSTOMER","CUSTOMER_CREDIT","BALANCE_DUE","MANUAL_REVIEW");
        if(!allowed.contains(normalized)) throw new IllegalArgumentException("Invalid financial resolution action");
        EngagementBillingSummaryResponse b=billing.get(r.getEngagement().getId());
        BigDecimal paid=settlements.sumPaidAmountByEngagementId(r.getEngagement().getId()); if(paid==null) paid=BigDecimal.ZERO;
        BigDecimal eligible=b.approvedBilling()==null?BigDecimal.ZERO:b.approvedBilling();
        BigDecimal balance=eligible.subtract(paid).max(BigDecimal.ZERO); BigDecimal refund=paid.subtract(eligible).max(BigDecimal.ZERO);
        BigDecimal resolvedAmount=amount==null?BigDecimal.ZERO:amount.max(BigDecimal.ZERO);
        if(normalized.equals("NO_ADJUSTMENT")||normalized.equals("MANUAL_REVIEW")) resolvedAmount=BigDecimal.ZERO;
        if((normalized.equals("REFUND_CUSTOMER")||normalized.equals("CUSTOMER_CREDIT")) && resolvedAmount.compareTo(refund)>0)
            throw new IllegalArgumentException("Refund/credit cannot exceed the calculated refund or credit due: "+refund);
        if(normalized.equals("BALANCE_DUE") && resolvedAmount.compareTo(balance)>0)
            throw new IllegalArgumentException("Balance due cannot exceed the calculated balance due: "+balance);
        ensureReconciliation(r);
        jdbc.update("UPDATE REPLACEMENT_FINANCIAL_RECONCILIATION SET RESOLUTION_STATUS='RESOLVED', RESOLUTION_ACTION=?, RESOLUTION_AMOUNT=?, RESOLUTION_NOTE=?, RESOLVED_AT=CURRENT_TIMESTAMP, RESOLVED_BY_USER_ID=? WHERE REPLACEMENT_REQUEST_ID=?",
                normalized, resolvedAmount, blankToNull(note), authorization.currentUser().getId(), id);
        return toResponse(r);
    }

    @Transactional
    public ExpertReplacementResponse assign(Long id, Long newExpertId) {
        requireAdmin();
        if (newExpertId == null) throw new IllegalArgumentException("newExpertId is required");
        ExpertReplacementRequest r=find(id);
        if (r.getStatus()!=ExpertReplacementStatus.APPROVED) throw new IllegalArgumentException("Only APPROVED replacement requests can be assigned");
        if (r.getNewEngagementId()!=null) throw new IllegalArgumentException("This replacement request has already been assigned");
        Map<String,Object> financial=financialData(r.getId());
        if(!"RESOLVED".equals(String.valueOf(financial.getOrDefault("RESOLUTION_STATUS","PENDING_REVIEW"))))
            throw new IllegalArgumentException("Resolve the financial treatment before assigning the replacement expert");
        if("MANUAL_REVIEW".equals(financial.get("RESOLUTION_ACTION")))
            throw new IllegalArgumentException("Financial treatment is marked MANUAL_REVIEW and must be completed before assignment");
        Engagement old=r.getEngagement();
        if (newExpertId.equals(old.getExpert().getId())) throw new IllegalArgumentException("The replacement expert must be different from the current expert");
        Expert newExpert=experts.findById(newExpertId).orElseThrow(()->new ResourceNotFoundException("Expert not found: "+newExpertId));
        if (newExpert.getStatus()!=ExpertStatus.ACTIVE) throw new IllegalArgumentException("Replacement expert must be ACTIVE");

        BigDecimal estimated=old.getConsultationRequest().getEstimatedHours()==null?BigDecimal.ZERO:old.getConsultationRequest().getEstimatedHours();
        BigDecimal logged=workLogs.sumHoursByEngagementId(old.getId());
        BigDecimal remaining=estimated.subtract(logged).max(BigDecimal.ZERO);
        if (remaining.signum()<=0) throw new IllegalArgumentException("No remaining engagement hours are available for replacement");

        ConsultationRequest replacementRequest=new ConsultationRequest();
        replacementRequest.setCustomer(old.getCustomer()); replacementRequest.setRequirement(old.getRequirement()); replacementRequest.setExpert(newExpert);
        replacementRequest.setMessage("Replacement assignment for engagement #"+old.getId()+"; original expert: "+old.getExpert().getFirstName()+" "+old.getExpert().getLastName());
        replacementRequest.setRequestedStartDate(old.getConsultationRequest().getRequestedStartDate()); replacementRequest.setEstimatedHours(remaining);
        BigDecimal oldRate=old.getConsultationRequest().getProposedRate(); String oldCurrency=old.getConsultationRequest().getCurrencyCode();
        replacementRequest.setProposedRate(oldRate!=null?oldRate:(newExpert.getConsulting()==null?null:newExpert.getConsulting().getHourlyRate()));
        replacementRequest.setCurrencyCode(oldCurrency!=null?oldCurrency:(newExpert.getConsulting()==null?"USD":newExpert.getConsulting().getCurrencyCode()));
        replacementRequest.setStatus(ConsultationRequestStatus.ACCEPTED); replacementRequest.setRespondedAt(LocalDateTime.now());
        ConsultationRequest savedRequest=consultationRequests.save(replacementRequest);
        Engagement newEngagement=engagementService.createFromAcceptedConsultation(savedRequest);
        newEngagement.setReplacementOfEngagementId(old.getId()); engagements.save(newEngagement);

        LocalDateTime now=LocalDateTime.now(); old.setStatus(EngagementStatus.REPLACED); old.setReplacedByEngagementId(newEngagement.getId()); old.setCancelledAt(null); engagements.save(old);
        assignmentHistory.findByEngagementIdOrderByEffectiveFromDesc(old.getId()).stream().filter(h -> "ASSIGNED".equals(h.getAction()) && h.getEffectiveTo()==null).findFirst().ifPresent(h -> {h.setEffectiveTo(now); assignmentHistory.save(h);});
        ExpertAssignmentHistory oldReplacementHistory=new ExpertAssignmentHistory(); oldReplacementHistory.setEngagement(old); oldReplacementHistory.setExpert(old.getExpert()); oldReplacementHistory.setAction("REPLACED"); oldReplacementHistory.setEffectiveFrom(now); oldReplacementHistory.setEffectiveTo(now); oldReplacementHistory.setReason("Replaced by expert #"+newExpertId+" through replacement request #"+id); oldReplacementHistory.setActor(authorization.currentUser()); assignmentHistory.save(oldReplacementHistory);
        ExpertAssignmentHistory newHistory=new ExpertAssignmentHistory(); newHistory.setEngagement(newEngagement); newHistory.setExpert(newExpert); newHistory.setAction("ASSIGNED"); newHistory.setEffectiveFrom(now); newHistory.setReason("Replacement for engagement #"+old.getId()+"; request #"+id); newHistory.setActor(authorization.currentUser()); assignmentHistory.save(newHistory);
        r.setNewExpert(newExpert); r.setNewEngagementId(newEngagement.getId()); r.setStatus(ExpertReplacementStatus.REPLACED); r.setReviewedBy(authorization.currentUser()); r.setReviewedAt(now);
        return toResponse(requests.save(r));
    }

    @Transactional public ExpertReplacementResponse reject(Long id,String reason){requireAdmin();if(reason==null||reason.isBlank())throw new IllegalArgumentException("A rejection reason is required");ExpertReplacementRequest r=find(id);if(r.getStatus()!=ExpertReplacementStatus.REQUESTED)throw new IllegalArgumentException("Only REQUESTED replacement requests can be rejected");r.setStatus(ExpertReplacementStatus.REJECTED);r.setReviewedBy(authorization.currentUser());r.setReviewedAt(LocalDateTime.now());r.setReviewerComment(reason.trim());return toResponse(requests.save(r));}
    @Transactional public ExpertReplacementResponse cancel(Long id){ExpertReplacementRequest r=find(id);authorization.assertCustomerOwns(r.getEngagement().getCustomer().getId());if(r.getStatus()!=ExpertReplacementStatus.REQUESTED)throw new IllegalArgumentException("Only REQUESTED replacement requests can be cancelled");r.setStatus(ExpertReplacementStatus.CANCELLED);r.setReviewedBy(authorization.currentUser());r.setReviewedAt(LocalDateTime.now());return toResponse(requests.save(r));}
    private ExpertReplacementRequest find(Long id){return requests.findWithDetailsById(id).orElseThrow(()->new ResourceNotFoundException("Replacement request not found: "+id));}
    private Engagement findEngagement(Long id){return engagements.findWithDetailsById(id).orElseThrow(()->new ResourceNotFoundException("Engagement not found: "+id));}
    private void requireAdmin(){if(!authorization.isAdmin())throw new AccessDeniedException("Admin access required");}
    private String blankToNull(String s){return s==null||s.isBlank()?null:s.trim();}

    private void refreshReconciliation(ExpertReplacementRequest r){
        EngagementBillingSummaryResponse b=billing.get(r.getEngagement().getId());
        BigDecimal paid=settlements.sumPaidAmountByEngagementId(r.getEngagement().getId()); if(paid==null)paid=BigDecimal.ZERO;
        BigDecimal eligible=b.approvedBilling()==null?BigDecimal.ZERO:b.approvedBilling();
        BigDecimal balance=eligible.subtract(paid).max(BigDecimal.ZERO); BigDecimal refund=paid.subtract(eligible).max(BigDecimal.ZERO);
        ensureReconciliation(r);
        jdbc.update("UPDATE REPLACEMENT_FINANCIAL_RECONCILIATION SET APPROVED_HOURS=?, ELIGIBLE_AMOUNT=?, PAID_AMOUNT=?, BALANCE_DUE=?, REFUND_OR_CREDIT_DUE=?, CURRENCY_CODE=?, CALCULATED_AT=CURRENT_TIMESTAMP WHERE REPLACEMENT_REQUEST_ID=?",
                b.approvedHours(),eligible,paid,balance,refund,b.currencyCode(),r.getId());
    }

    private void ensureReconciliation(ExpertReplacementRequest r){
        Integer count=jdbc.queryForObject("SELECT COUNT(*) FROM REPLACEMENT_FINANCIAL_RECONCILIATION WHERE REPLACEMENT_REQUEST_ID=?",Integer.class,r.getId());
        if(count==null||count==0){
            EngagementBillingSummaryResponse b=billing.get(r.getEngagement().getId());
            BigDecimal paid=settlements.sumPaidAmountByEngagementId(r.getEngagement().getId()); if(paid==null)paid=BigDecimal.ZERO;
            BigDecimal eligible=b.approvedBilling()==null?BigDecimal.ZERO:b.approvedBilling();
            jdbc.update("INSERT INTO REPLACEMENT_FINANCIAL_RECONCILIATION (REPLACEMENT_REQUEST_ID,APPROVED_HOURS,ELIGIBLE_AMOUNT,PAID_AMOUNT,BALANCE_DUE,REFUND_OR_CREDIT_DUE,CURRENCY_CODE,RESOLUTION_STATUS) VALUES (?,?,?,?,?,?,?,'PENDING_REVIEW')",
                    r.getId(),b.approvedHours(),eligible,paid,eligible.subtract(paid).max(BigDecimal.ZERO),paid.subtract(eligible).max(BigDecimal.ZERO),b.currencyCode());
        }
    }

    private Map<String,Object> financialData(Long requestId){
        List<Map<String,Object>> rows=jdbc.queryForList("SELECT RESOLUTION_STATUS,RESOLUTION_ACTION,RESOLUTION_AMOUNT,RESOLUTION_NOTE,RESOLVED_AT FROM REPLACEMENT_FINANCIAL_RECONCILIATION WHERE REPLACEMENT_REQUEST_ID=?",requestId);
        return rows.isEmpty()?Map.of("RESOLUTION_STATUS","PENDING_REVIEW"):rows.get(0);
    }

    private ExpertReplacementResponse toResponse(ExpertReplacementRequest r){
        EngagementBillingSummaryResponse b=billing.get(r.getEngagement().getId());
        BigDecimal paid=settlements.sumPaidAmountByEngagementId(r.getEngagement().getId());if(paid==null)paid=BigDecimal.ZERO;
        BigDecimal eligible=b.approvedBilling()==null?BigDecimal.ZERO:b.approvedBilling();BigDecimal balance=eligible.subtract(paid).max(BigDecimal.ZERO);BigDecimal refund=paid.subtract(eligible).max(BigDecimal.ZERO);
        Map<String,Object> f=financialData(r.getId());
        String resolutionStatus=String.valueOf(f.getOrDefault("RESOLUTION_STATUS","PENDING_REVIEW"));
        String resolutionAction=(String)f.get("RESOLUTION_ACTION"); BigDecimal resolutionAmount=(BigDecimal)f.get("RESOLUTION_AMOUNT"); String resolutionNote=(String)f.get("RESOLUTION_NOTE");
        Object resolved=f.get("RESOLVED_AT"); LocalDateTime resolvedAt=resolved instanceof Timestamp t?t.toLocalDateTime():resolved instanceof java.sql.Date d?d.toLocalDate().atStartOfDay():null;
        Long newExpertId=r.getNewExpert()==null?null:r.getNewExpert().getId();String newExpertName=r.getNewExpert()==null?null:r.getNewExpert().getFirstName()+" "+r.getNewExpert().getLastName();
        return new ExpertReplacementResponse(r.getId(),r.getEngagement().getId(),r.getEngagement().getRequirement().getId(),r.getEngagement().getRequirement().getTitle(),r.getCurrentExpert().getId(),r.getCurrentExpert().getFirstName()+" "+r.getCurrentExpert().getLastName(),r.getStatus().name(),r.getReasonCode().name(),r.getComments(),r.getRequestedAt(),r.getWorkCutoffAt(),r.getReviewedAt(),r.getReviewerComment(),b.approvedHours(),eligible,paid,balance,refund,b.currencyCode(),resolutionStatus,resolutionAction,resolutionAmount,resolutionNote,resolvedAt,newExpertId,newExpertName,r.getNewEngagementId());
    }
}
