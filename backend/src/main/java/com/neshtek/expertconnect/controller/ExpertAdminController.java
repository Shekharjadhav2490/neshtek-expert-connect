package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.ExpertRejectRequest;
import com.neshtek.expertconnect.dto.ExpertResponse;
import com.neshtek.expertconnect.dto.ExpertVerificationRequest;
import com.neshtek.expertconnect.service.ExpertService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/experts")
public class ExpertAdminController {
    private final ExpertService service;

    public ExpertAdminController(ExpertService service) { this.service = service; }

    @PostMapping("/{id}/start-review")
    public ExpertResponse startReview(@PathVariable Long id, @RequestParam String reviewer) {
        return service.startReview(id, reviewer);
    }

    @PostMapping("/{id}/start-verification")
    public ExpertResponse startVerification(@PathVariable Long id, @RequestParam String reviewer) {
        return service.startVerification(id, reviewer);
    }

    @PostMapping("/{id}/verify")
    public ExpertResponse verify(@PathVariable Long id, @Valid @RequestBody ExpertVerificationRequest request) {
        return service.verify(id, request);
    }

    @PostMapping("/{id}/approve")
    public ExpertResponse activate(@PathVariable Long id, @RequestParam String reviewer) {
        return service.activate(id, reviewer);
    }

    @PostMapping("/{id}/reject")
    public ExpertResponse reject(@PathVariable Long id, @Valid @RequestBody ExpertRejectRequest request, @RequestParam String reviewer) {
        return service.reject(id, request, reviewer);
    }
}
