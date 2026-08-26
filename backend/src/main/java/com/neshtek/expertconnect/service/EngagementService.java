package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.EngagementResponse;
import com.neshtek.expertconnect.entity.ConsultationRequest;
import com.neshtek.expertconnect.entity.ConsultationRequestStatus;
import com.neshtek.expertconnect.entity.Engagement;
import com.neshtek.expertconnect.entity.EngagementStatus;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.EngagementRepository;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EngagementService {
    private final EngagementRepository repository;
    private final ResourceAuthorizationService authorization;

    public EngagementService(EngagementRepository repository, ResourceAuthorizationService authorization) {
        this.repository = repository;
        this.authorization = authorization;
    }

    @Transactional
    public Engagement createFromAcceptedConsultation(ConsultationRequest request) {
        if (request.getStatus() != ConsultationRequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("Only ACCEPTED consultation requests can create an engagement");
        }
        if (repository.existsByConsultationRequestId(request.getId())) {
            return repository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Engagement not found for consultation request: " + request.getId()));
        }

        Engagement engagement = new Engagement();
        engagement.setConsultationRequest(request);
        engagement.setCustomer(request.getCustomer());
        engagement.setExpert(request.getExpert());
        engagement.setRequirement(request.getRequirement());
        engagement.setStatus(EngagementStatus.READY);
        return repository.save(engagement);
    }

    @Transactional(readOnly = true)
    public EngagementResponse get(Long id) {
        Engagement engagement = find(id);
        authorization.assertCanAccess(engagement);
        return toResponse(engagement);
    }

    @Transactional(readOnly = true)
    public Page<EngagementResponse> byCustomer(Long customerId, Pageable pageable) {
        authorization.assertCustomerOwns(customerId);
        return repository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EngagementResponse> byExpert(Long expertId, Pageable pageable) {
        authorization.assertExpertOwns(expertId);
        return repository.findByExpertIdOrderByCreatedAtDesc(expertId, pageable).map(this::toResponse);
    }

    @Transactional
    public EngagementResponse start(Long id) {
        Engagement engagement = find(id);
        authorization.assertExpertOwns(engagement.getExpert().getId());
        if (engagement.getStatus() != EngagementStatus.READY) {
            throw new IllegalArgumentException("Only READY engagements can be started");
        }
        engagement.setStatus(EngagementStatus.ACTIVE);
        engagement.setStartedAt(LocalDateTime.now());
        return toResponse(repository.save(engagement));
    }

    @Transactional
    public EngagementResponse complete(Long id) {
        Engagement engagement = find(id);
        authorization.assertExpertOwns(engagement.getExpert().getId());
        if (engagement.getStatus() != EngagementStatus.ACTIVE) {
            throw new IllegalArgumentException("Only ACTIVE engagements can be completed");
        }
        engagement.setStatus(EngagementStatus.COMPLETED);
        engagement.setCompletedAt(LocalDateTime.now());
        return toResponse(repository.save(engagement));
    }

    @Transactional
    public EngagementResponse cancel(Long id) {
        Engagement engagement = find(id);
        authorization.assertCanAccess(engagement);
        if (engagement.getStatus() != EngagementStatus.READY && engagement.getStatus() != EngagementStatus.ACTIVE) {
            throw new IllegalArgumentException("Only READY or ACTIVE engagements can be cancelled");
        }
        engagement.setStatus(EngagementStatus.CANCELLED);
        engagement.setCancelledAt(LocalDateTime.now());
        return toResponse(repository.save(engagement));
    }

    private Engagement find(Long id) {
        return repository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + id));
    }

    private EngagementResponse toResponse(Engagement e) {
        ConsultationRequest request = e.getConsultationRequest();
        String expertName = e.getExpert().getFirstName() + " " + e.getExpert().getLastName();
        return new EngagementResponse(
                e.getId(),
                request.getId(),
                e.getCustomer().getId(),
                e.getExpert().getId(),
                expertName,
                e.getRequirement().getId(),
                e.getRequirement().getTitle(),
                e.getStatus().name(),
                request.getRequestedStartDate(),
                request.getEstimatedHours(),
                request.getProposedRate(),
                request.getCurrencyCode(),
                e.getStartedAt(),
                e.getCompletedAt(),
                e.getCancelledAt(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
