package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.WorkLogRequest;
import com.neshtek.expertconnect.dto.WorkLogResponse;
import com.neshtek.expertconnect.service.EngagementWorkLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/engagements/{engagementId}/work-logs")
public class EngagementWorkLogController {
    private final EngagementWorkLogService service;

    public EngagementWorkLogController(EngagementWorkLogService service) {
        this.service = service;
    }

    @GetMapping
    public Page<WorkLogResponse> list(@PathVariable Long engagementId, Pageable pageable) {
        return service.list(engagementId, pageable);
    }

    @GetMapping("/total-hours")
    public BigDecimal totalHours(@PathVariable Long engagementId) {
        return service.totalHours(engagementId);
    }

    @PostMapping
    public WorkLogResponse create(@PathVariable Long engagementId, @RequestBody WorkLogRequest request) {
        return service.create(engagementId, request);
    }
}
