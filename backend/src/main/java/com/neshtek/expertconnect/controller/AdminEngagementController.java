package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.AdminEngagementResponse;
import com.neshtek.expertconnect.entity.Engagement;
import com.neshtek.expertconnect.repository.EngagementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/engagements")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEngagementController {
    private final EngagementRepository repository;

    public AdminEngagementController(EngagementRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<AdminEngagementResponse> all(Pageable pageable) {
        return repository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    private AdminEngagementResponse toResponse(Engagement e) {
        String expertName = e.getExpert().getFirstName() + " " + e.getExpert().getLastName();
        return new AdminEngagementResponse(
                e.getId(),
                e.getConsultationRequest().getId(),
                e.getCustomer().getId(),
                e.getCustomer().getCompanyName(),
                e.getExpert().getId(),
                expertName,
                e.getRequirement().getId(),
                e.getRequirement().getTitle(),
                e.getStatus().name(),
                e.getConsultationRequest().getRequestedStartDate(),
                e.getConsultationRequest().getEstimatedHours(),
                e.getConsultationRequest().getProposedRate(),
                e.getConsultationRequest().getCurrencyCode(),
                e.getStartedAt(),
                e.getCompletedAt(),
                e.getCancelledAt(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
