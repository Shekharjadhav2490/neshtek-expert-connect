package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.CustomerRequirementResponse;
import com.neshtek.expertconnect.service.CustomerRequirementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/requirements")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRequirementController {
    private final CustomerRequirementService service;

    public AdminRequirementController(CustomerRequirementService service) {
        this.service = service;
    }

    @GetMapping
    public Page<CustomerRequirementResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return service.list(PageRequest.of(safePage, safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping("/{id}")
    public CustomerRequirementResponse get(@PathVariable Long id) {
        return service.get(id);
    }
}
