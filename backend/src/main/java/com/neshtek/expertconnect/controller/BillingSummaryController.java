package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.EngagementBillingSummaryResponse;
import com.neshtek.expertconnect.service.BillingSummaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/billing")
public class BillingSummaryController {
    private final BillingSummaryService service;
    public BillingSummaryController(BillingSummaryService service) { this.service = service; }
    @GetMapping("/engagements/{engagementId}")
    public EngagementBillingSummaryResponse engagement(@PathVariable Long engagementId) { return service.get(engagementId); }
}
