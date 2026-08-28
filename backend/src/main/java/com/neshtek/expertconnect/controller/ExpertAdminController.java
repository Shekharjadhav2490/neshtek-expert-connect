package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.ExpertResponse;
import com.neshtek.expertconnect.service.ExpertService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/{id}/approve")
    public ExpertResponse activate(@PathVariable Long id, @RequestParam String reviewer) {
        return service.activate(id, reviewer);
    }
}
