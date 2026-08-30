package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.EngagementBillingSummaryResponse;
import com.neshtek.expertconnect.dto.ExpertReplacementRequestDto;
import com.neshtek.expertconnect.dto.ExpertReplacementResponse;
import com.neshtek.expertconnect.entity.AppUser;
import com.neshtek.expertconnect.entity.Engagement;
import com.neshtek.expertconnect.entity.EngagementStatus;
import com.neshtek.expertconnect.entity.ExpertReplacementRequest;
import com.neshtek.expertconnect.entity.ExpertReplacementStatus;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.EngagementRepository;
import com.neshtek.expertconnect.repository.ExpertReplacementRequestRepository;
import com.neshtek.expertconnect.repository.ExpertAssignmentHistoryRepository;
import com.neshtek.expertconnect.repository.SettlementRepository;
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

    public ExpertReplacementService(ExpertReplacementRequestRepository requests, EngagementRepository engagements,
                                    ExpertAssignmentHistoryRepository assignmentHistory, SettlementRepository settlements,
                                    BillingSummaryService billing, ResourceAuthorizationService authorization) {
        this.requests=requests; this.engagements=engagements; this.assignmentHistory=assignmentHistory;
        this.settlements=settlements; this.billing=billing; this.authorization=authorization;
    }

    @Transactional
    public ExpertReplacementResponse request(Long engagementId, ExpertReplacementRequestDto dto) {
        Engagement e=findEngagement(engagementId);
        authorization.assertCustomerOwns(e.getCustomer().getId());
        if (e.getStatus()!= EngagementStatus.READY && e.getStatus()!= EngagementStatus.ACTIVE && e.getStatus()!= EngagementStatus.PAUSED)
            throw new IllegalArgumentException("Expert replacement can only be requested for READY, ACTIVE or PAUSED engagements");
        if (requests.existsByEngagementIdAndStatus(engagementId, ExpertReplacementStatus.REQUESTED))
            throw new IllegalArgumentException("A replacement request is already pending for this engagement");
        AppUser actor=authorization.currentUser();
        ExpertReplacementRequest r=new ExpertReplacementRequest();
        r.setEngagement(e); r.setCurrentExpert(e.getExpert()); r.setRequestedBy(actor);
        r.setReasonCode(dto.reasonCode()); r.setComments(dto.comments().trim()); r.setStatus(ExpertReplacementStatus.REQUESTED);
        r.setWorkCutoffAt(LocalDateTime.now());
        ExpertReplacementRequest saved=requests.save(r);
        var h=new com.neshtek.expertconnect.entity.ExpertAssignmentHistory();
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

    @Transactional
    public ExpertReplacementResponse approve(Long id, String comment) {
        requireAdmin(); ExpertReplacementRequest r=find(id);
        if(r.getStatus()!=ExpertReplacementStatus.REQUESTED) throw new IllegalArgumentException("Only REQUESTED replacement requests can be approved");
        r.setStatus(ExpertReplacementStatus.APPROVED); r.setReviewedBy(authorization.currentUser()); r.setReviewedAt(LocalDateTime.now());
        r.setReviewerComment(blankToNull(comment));
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
        return new ExpertReplacementResponse(r.getId(),r.getEngagement().getId(),r.getEngagement().getRequirement().getId(),r.getEngagement().getRequirement().getTitle(),
                r.getCurrentExpert().getId(),r.getCurrentExpert().getFirstName()+" "+r.getCurrentExpert().getLastName(),r.getStatus().name(),r.getReasonCode().name(),r.getComments(),
                r.getRequestedAt(),r.getWorkCutoffAt(),r.getReviewedAt(),r.getReviewerComment(),b.approvedHours(),eligible,paid,balance,refund,b.currencyCode());
    }
}
