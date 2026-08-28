package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.SettlementResponse;
import com.neshtek.expertconnect.service.SettlementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementController {
    private final SettlementService service;
    public SettlementController(SettlementService service) { this.service = service; }

    @GetMapping("/experts/{expertId}")
    @PreAuthorize("hasAnyRole('EXPERT','ADMIN')")
    public Page<SettlementResponse> byExpert(@PathVariable Long expertId, Pageable pageable) {
        return service.byExpert(expertId, pageable);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<SettlementResponse> all(Pageable pageable) { return service.all(pageable); }

    @GetMapping("/{id}")
    public SettlementResponse get(@PathVariable Long id) { return service.get(id); }

    @PostMapping("/engagements/{engagementId}/request")
    @PreAuthorize("hasRole('EXPERT')")
    public SettlementResponse request(@PathVariable Long engagementId) { return service.request(engagementId); }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public SettlementResponse approve(@PathVariable Long id, @RequestParam(required=false) String comment) { return service.approve(id, comment); }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public SettlementResponse reject(@PathVariable Long id, @RequestParam String reason) { return service.reject(id, reason); }

    @PostMapping("/{id}/paid")
    @PreAuthorize("hasRole('ADMIN')")
    public SettlementResponse paid(@PathVariable Long id, @RequestParam String paymentReference) { return service.markPaid(id, paymentReference); }
}
