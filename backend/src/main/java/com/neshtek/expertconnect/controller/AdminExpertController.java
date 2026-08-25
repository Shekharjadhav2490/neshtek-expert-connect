package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.ExpertRejectRequest;
import com.neshtek.expertconnect.dto.ExpertResponse;
import com.neshtek.expertconnect.dto.ExpertVerificationRequest;
import com.neshtek.expertconnect.service.ExpertService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/experts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminExpertController {
    private final ExpertService service;
    public AdminExpertController(ExpertService service) { this.service = service; }

    @GetMapping
    public Page<ExpertResponse> list(@RequestParam(required = false) String status,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("Invalid page or size");
        return service.search(null, status, null, null, null,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping("/{id}")
    public ExpertResponse get(@PathVariable Long id) { return service.get(id); }

    @PostMapping("/{id}/review")
    public ExpertResponse review(@PathVariable Long id, Authentication authentication) {
        return service.startReview(id, "ADMIN-" + authentication.getName());
    }

    @PostMapping("/{id}/verification")
    public ExpertResponse verification(@PathVariable Long id, Authentication authentication) {
        return service.startVerification(id, "ADMIN-" + authentication.getName());
    }

    @PostMapping("/{id}/verify")
    public ExpertResponse verify(@PathVariable Long id, @Valid @RequestBody ExpertVerificationRequest request) {
        return service.verify(id, request);
    }

    @PostMapping("/{id}/activate")
    public ExpertResponse activate(@PathVariable Long id, Authentication authentication) {
        return service.activate(id, "ADMIN-" + authentication.getName());
    }

    @PostMapping("/{id}/reject")
    public ExpertResponse reject(@PathVariable Long id, @Valid @RequestBody ExpertRejectRequest request, Authentication authentication) {
        return service.reject(id, request, "ADMIN-" + authentication.getName());
    }
}
