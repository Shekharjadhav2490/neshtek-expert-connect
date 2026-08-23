package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.ExpertMatchResponse;
import com.neshtek.expertconnect.service.ExpertMatchingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requirements")
public class ExpertMatchingController {
    private final ExpertMatchingService matchingService;

    public ExpertMatchingController(ExpertMatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/{requirementId}/matches")
    public List<ExpertMatchResponse> matches(
            @PathVariable Long requirementId,
            @RequestParam(defaultValue = "5") int limit) {
        return matchingService.findMatches(requirementId, limit);
    }

    @GetMapping("/api/v1/customers/{customerId}/requirements/{requirementId}/matches")
    public List<ExpertMatchResponse> customerMatches(
            @PathVariable Long customerId,
            @PathVariable Long requirementId,
            @RequestParam(defaultValue = "5") int limit) {
        return matchingService.findMatchesForCustomer(customerId, requirementId, limit);
    }
}
