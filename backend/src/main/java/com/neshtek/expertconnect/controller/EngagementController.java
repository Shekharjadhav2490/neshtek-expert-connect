package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.EngagementResponse;
import com.neshtek.expertconnect.service.EngagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/engagements")
public class EngagementController {
    private final EngagementService service;

    public EngagementController(EngagementService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public EngagementResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/customers/{customerId}")
    public Page<EngagementResponse> byCustomer(@PathVariable Long customerId, Pageable pageable) {
        return service.byCustomer(customerId, pageable);
    }

    @GetMapping("/experts/{expertId}")
    public Page<EngagementResponse> byExpert(@PathVariable Long expertId, Pageable pageable) {
        return service.byExpert(expertId, pageable);
    }

    @PostMapping("/{id}/start")
    public EngagementResponse start(@PathVariable Long id) {
        return service.start(id);
    }

    @PostMapping("/{id}/pause")
    public EngagementResponse pause(@PathVariable Long id, @RequestParam String reason) {
        return service.pause(id, reason);
    }

    @PostMapping("/{id}/resume")
    public EngagementResponse resume(@PathVariable Long id) {
        return service.resume(id);
    }

    @PostMapping("/{id}/complete")
    public EngagementResponse complete(@PathVariable Long id) {
        return service.complete(id);
    }

    @PostMapping("/{id}/cancel")
    public EngagementResponse cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}
