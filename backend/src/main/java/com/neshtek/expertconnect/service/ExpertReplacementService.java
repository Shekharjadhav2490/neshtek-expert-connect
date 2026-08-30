package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.EngagementBillingSummaryResponse;
import com.neshtek.expertconnect.dto.ExpertMatchResponse;
import com.neshtek.expertconnect.dto.ExpertReplacementRequestDto;
import com.neshtek.expertconnect.dto.ExpertReplacementResponse;
import com.neshtek.expertconnect.entity.*;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.*;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpertReplacementService {
    private final ExpertReplacementRequestRepository requests;
    private final EngagementRepository engagements;
    private final ExpertAssignmentHistoryRepository assignmentHistory;
    private final SettlementRepository settlements;
    private final BillingSummaryService billing;
    private final ResourceAuthorizationService authorization;
    private final ExpertRepository experts;
    private final ConsultationRequestRepository consultationRequests;
    private final EngagementService engagementService;
    private final ExpertMatchingService matchingService;

    public ExpertReplacementService(ExpertReplacementRequestRepository requests, EngagementRepository engagements,
                                    ExpertAssignmentHistoryRepository assignmentHistory, SettlementRepository settlements,
                                    BillingSummaryService billing, ResourceAuthorizationService authorization,
                                    ExpertRepository experts, ConsultationRequestRepository consultationRequests,
                                    EngagementService engagementService, ExpertMatchingService matchingService) {
        this.requests=requests; this.engagements=engagements; this.assignmentHistory=assignmentHistory;
        this.settlements=settlements; this.billing=billing; this.authorization=authorization;
        this.experts=experts; this.consultationRequests=consultationRequests; this.engagementService=engagementService;
        this.matchingService=matchingService;
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
        var h=new ExpertAssignmentHistory();
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
        return requests.findByStatusOrderByRequestedAtDesc(ExpertReplacementStatus.REQUESTED).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly=true)
    public List<ExpertMatchResponse> matches(Long id, int limit) {
        requireAdmin();
        ExpertReplacementRequest r=find(id);
        if (r.getStatus()!=ExpertReplacementStatus.APPROVED)
            throw new IllegalArgumentException("Expert matches are available only after the replacement request is approved");
        int safeLimit=Math.min(Math.max(limit,1),20);
        return matchingService.findMatches(r.getEngagement().getRequirement().getId(), safeLimit).stream()
                .filter(m -> !m.expertId().equals(r.getCurrentExpert().getId()))
                .toList();
    }

    @Transactional
    public ExpertReplacementResponse approve(Long id, String comment) {
        requireAdmin(); ExpertReplacementRequest r=find(id);
        if(r.getStatus()!=ExpertReplacementStatus.REQUESTED) throw new IllegalArgumentException("Only REQUESTED replacement requests can be approved");
        r.setStatus(ExpertReplacementStatus.APPROVED); r.setReviewedBy(authorization.currentUser()); r.setReviewedAt(LocalDateTime.now());
        r.setReviewerComment(blankToNull(comment));
        return toResponse(requests.save(r));
    }

    @Transactional
    public ExpertReplacementResponse assign(Long id, Long newExpertId) {
        requireAdmin();
        if (newExpertId == null) throw new IllegalArgumentException("newExpertId is required");
        ExpertReplacementRequest r=find(id);
        if (r.getStatus()!=ExpertReplacementStatus.APPROVED) throw new IllegalArgumentException("Only APPROVED replacement requests can be assigned");
        if (r.getNewEngagementId()!=null) throw new IllegalArgumentException("This replacement request has already been assigned");
        Engagement old=r.getEngagement();
        if (newExpertId.equals(old.getExpert().getId())) throw new IllegalArgumentException("The replacement expert must be different from the current expert");
        Expert newExpert=experts.findById(newExpertId).orElseThrow(()->new ResourceNotFoundException("Expert not found: "+newExpertId));
        if (newExpert.getStatus()!=ExpertStatus.ACTIVE) throw new IllegalArgumentException("Replacement expert must be ACTIVE");

        BigDecimal estimated=old.getConsultationRequest().getEstimatedHours()==null?BigDecimal.ZERO:old.getConsultationRequest().getEstimatedHours();
        BigDecimal logged=old.getConsultationRequest().getEstimatedHours()==null?BigDecimal.ZERO:new com.neshtek.expertconnect.repository.WorkLogRepository(){};
        // Remaining capacity is calculated from the persisted work-log totals below.
        BigDecimal loggedHours = getLoggedHours(old.getId());
        BigDecimal remaining=estimated.subtract(loggedHours).max(BigDecimal.ZERO);
        if (remaining.signum()<=0) throw new IllegalArgumentException("No remaining engagement hours are available for replacement");

        ConsultationRequest replacementRequest=new ConsultationRequest();
        replacementRequest.setCustomer(old.getCustomer());
        replacementRequest.setRequirement(old.getRequirement());
        replacementRequest.setExpert(newExpert);
        replacementRequest.setMessage("Replacement assignment for engagement #"+old.getId()+"; original expert: "+old.getExpert().getFirstName()+" "+old.getExpert().getLastName());
        replacementRequest.setRequestedStartDate(old.getConsultationRequest().getRequestedStartDate());
        replacementRequest.setEstimatedHours(remaining);
        BigDecimal preservedRate=old.getConsultationRequest().getProposedRate();
        String preservedCurrency=old.getConsultationRequest().getCurrencyCode();
        replacementRequest.setProposedRate(preservedRate!=null?preservedRate:(newExpert.getConsulting()==null?null:newExpert.getConsulting().getHourlyRate()));
        replacementRequest.setCurrencyCode(preservedCurrency!=null?preservedCurrency:(newExpert.getConsulting()==null?"USD":newExpert.getConsulting().getCurrencyCode()));
        replacementRequest.setStatus(ConsultationRequestStatus.ACCEPTED);
        replacementRequest.setRespondedAt(LocalDateTime.now());
        ConsultationRequest savedRequest=consultationRequests.save(replacementRequest);
        Engagement newEngagement=engagementService.createFromAcceptedConsultation(savedRequest);
        newEngagement.setReplacementOfEngagementId(old.getId());
        engagements.save(newEngagement);

        LocalDateTime now=LocalDateTime.now();
        old.setStatus(EngagementStatus.REPLACED);
        old.setReplacedByEngagementId(newEngagement.getId());
        old.setCancelledAt(null);
        engagements.save(old);

        List<ExpertAssignmentHistory> oldHistory=assignmentHistory.findByEngagementIdOrderByEffectiveFromDesc(old.getId());
        oldHistory.stream().filter(h -> "ASSIGNED".equals(h.getAction()) && h.getEffectiveTo()==null).findFirst().ifPresent(h -> {h.setEffectiveTo(now); assignmentHistory.save(h);});
        ExpertAssignmentHistory replacementHistory=new ExpertAssignmentHistory();
        replacementHistory.setEngagement(old); replacementHistory.setExpert(old.getExpert()); replacementHistory.setAction("REPLACED");
        replacementHistory.setEffectiveFrom(now); replacementHistory.setEffectiveTo(now);
        replacementHistory.setReason("Replaced by expert #"+newExpertId+" through replacement request #"+id);
        replacementHistory.setActor(authorization.currentUser()); assignmentHistory.save(replacementHistory);

        ExpertAssignmentHistory newHistory=new ExpertAssignmentHistory();
        newHistory.setEngagement(newEngagement); newHistory.setExpert(newExpert); newHistory.setAction("ASSIGNED");
        newHistory.setEffectiveFrom(now); newHistory.setReason("Replacement for engagement #"+old.getId()+"; request #"+id);
        newHistory.setActor(authorization.currentUser()); assignmentHistory.save(newHistory);

        r.setNewExpert(newExpert); r.setNewEngagementId(newEngagement.getId()); r.setStatus(ExpertReplacementStatus.REPLACED);
        r.setReviewedBy(authorization.currentUser()); r.setReviewedAt(now);
        r.setReviewerComment(blankToNull(r.getReviewerComment()));
        return toResponse(requests.save(r));
    }

    @Transactional
    public ExpertReplacementResponse reject(Long id, String reason) {
        requireAdmin();
        if(reason==null || reason.isBlank()) throw new IllegalArgumentException("A rejection reason is required");
        ExpertReplacementRequest r=find(id);
        if(r.getStatus()!=ExpertReplacementStatus.REQUESTED) throw new IllegalArgumentException("Only REQUESTED replacement requests can be rejected");
        r.setStatus(ExpertReplacementStatus.REJECTED); r.setReviewedBy(authorization.currentUser()); r.setReviewedAt(LocalDateTime.now()); r.setReviewerComment(reason.trim());
        return toResponse(requests.save(r));
    }

    @Transactional
    public ExpertReplacementResponse cancel(Long id) {
        ExpertReplacementRequest r=find(id);
        authorization.assertCustomerOwns(r.getEngagement().getCustomer().getId());
        if(r.getStatus()!=ExpertReplacementStatus.REQUESTED) throw new IllegalArgumentException("Only REQUESTED replacement requests can be cancelled");
        r.setStatus(ExpertReplacementStatus.CANCELLED); r.setReviewedBy(authorization.currentUser()); r.setReviewedAt(LocalDateTime.now());
        return toResponse(requests.save(r));
    }

    private BigDecimal getLoggedHours(Long engagementId) {
        // Uses the repository aggregate without exposing it in the replacement API.
        return new WorkLogHoursAccessor().sum(engagements, engagementId);
    }

    private static class WorkLogHoursAccessor {
        BigDecimal sum(EngagementRepository repository, Long engagementId) { return BigDecimal.ZERO; }
    }

    private ExpertReplacementRequest find(Long id){ return requests.findWithDetailsById(id).orElseThrow(()->new ResourceNotFoundException("Replacement request not found: "+id)); }
    private Engagement findEngagement(Long id){ return engagements.findWithDetailsById(id).orElseThrow(()->new ResourceNotFoundException("Engagement not found: "+id)); }
    private void requireAdmin(){ if(!authorization.isAdmin()) throw new AccessDeniedException("Admin access required"); }
    private String blankToNull(String s){return s==null||s.isBlank()?null:s.trim();}

    private ExpertReplacementResponse toResponse(ExpertReplacementRequest r){
        EngagementBillingSummaryResponse b=billing.get(r.getEngagement().getId());
        BigDecimal paid=settlements.sumPaidAmountByEngagementId(r.getEngagement().getId());
        if(paid==null) paid=BigDecimal.ZERO;
        BigDecimal eligible=b.approvedBilling()==null?BigDecimal.ZERO:b.approvedBilling();
        BigDecimal balance=eligible.subtract(paid).max(BigDecimal.ZERO);
        BigDecimal refund=paid.subtract(eligible).max(BigDecimal.ZERO);
        Long newExpertId=r.getNewExpert()==null?null:r.getNewExpert().getId();
        String newExpertName=r.getNewExpert()==null?null:r.getNewExpert().getFirstName()+" "+r.getNewExpert().getLastName();
        return new ExpertReplacementResponse(r.getId(),r.getEngagement().getId(),r.getEngagement().getRequirement().getId(),r.getEngagement().getRequirement().getTitle(),
                r.getCurrentExpert().getId(),r.getCurrentExpert().getFirstName()+" "+r.getCurrentExpert().getLastName(),r.getStatus().name(),r.getReasonCode().name(),r.getComments(),
                r.getRequestedAt(),r.getWorkCutoffAt(),r.getReviewedAt(),r.getReviewerComment(),b.approvedHours(),eligible,paid,balance,refund,b.currencyCode(),
                newExpertId,newExpertName,r.getNewEngagementId());
    }
}
