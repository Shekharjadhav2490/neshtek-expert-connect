package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.ConsultationRequestResponse;
import com.neshtek.expertconnect.entity.ConsultationRequest;
import com.neshtek.expertconnect.repository.ConsultationRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/consultation-requests")
@PreAuthorize("hasRole('ADMIN')")
public class AdminConsultationRequestController {
    private final ConsultationRequestRepository repository;

    public AdminConsultationRequestController(ConsultationRequestRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<ConsultationRequestResponse> all(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    private ConsultationRequestResponse toResponse(ConsultationRequest e) {
        String expertName = e.getExpert().getFirstName() + " " + e.getExpert().getLastName();
        return new ConsultationRequestResponse(
                e.getId(),
                e.getCustomer().getId(),
                e.getRequirement().getId(),
                e.getExpert().getId(),
                expertName,
                e.getRequirement().getTitle(),
                e.getMessage(),
                e.getRequestedStartDate(),
                e.getEstimatedHours(),
                e.getProposedRate(),
                e.getCurrencyCode(),
                e.getStatus().name(),
                e.getRejectionReason(),
                e.getRespondedAt(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
