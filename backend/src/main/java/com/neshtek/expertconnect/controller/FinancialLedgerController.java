package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.FinancialLedgerAdjustmentRequest;
import com.neshtek.expertconnect.dto.FinancialLedgerEntryResponse;
import com.neshtek.expertconnect.dto.FinancialReconciliationResponse;
import com.neshtek.expertconnect.service.FinancialLedgerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/financial-ledger")
public class FinancialLedgerController {
    private final FinancialLedgerService service;
    public FinancialLedgerController(FinancialLedgerService service) { this.service = service; }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public Page<FinancialLedgerEntryResponse> customer(@PathVariable Long customerId, Pageable pageable) { return service.byCustomer(customerId, pageable); }

    @GetMapping("/expert/{expertId}")
    @PreAuthorize("hasAnyRole('EXPERT','ADMIN')")
    public Page<FinancialLedgerEntryResponse> expert(@PathVariable Long expertId, Pageable pageable) { return service.byExpert(expertId, pageable); }

    @GetMapping("/engagement/{engagementId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','EXPERT','ADMIN')")
    public Page<FinancialLedgerEntryResponse> engagement(@PathVariable Long engagementId, Pageable pageable) { return service.byEngagement(engagementId, pageable); }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<FinancialLedgerEntryResponse> all(Pageable pageable) { return service.all(pageable); }

    @GetMapping("/customer/{customerId}/reconciliation")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public FinancialReconciliationResponse customerReconciliation(@PathVariable Long customerId) { return service.reconcileCustomer(customerId); }

    @GetMapping("/expert/{expertId}/reconciliation")
    @PreAuthorize("hasAnyRole('EXPERT','ADMIN')")
    public FinancialReconciliationResponse expertReconciliation(@PathVariable Long expertId) { return service.reconcileExpert(expertId); }

    @PostMapping("/adjustments")
    @PreAuthorize("hasRole('ADMIN')")
    public FinancialLedgerEntryResponse adjustment(@Valid @RequestBody FinancialLedgerAdjustmentRequest request) { return service.createAdjustment(request); }
}
