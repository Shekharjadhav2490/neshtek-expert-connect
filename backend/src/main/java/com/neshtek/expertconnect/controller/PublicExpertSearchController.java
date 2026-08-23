package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.PublicExpertSearchResponse;
import com.neshtek.expertconnect.service.PublicExpertSearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/public/experts")
public class PublicExpertSearchController {
    private final PublicExpertSearchService service;

    public PublicExpertSearchController(PublicExpertSearchService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PublicExpertSearchResponse>> search(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) BigDecimal minExperience,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) BigDecimal maxHourlyRate,
            @RequestParam(required = false) String currency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (page < 0) throw new IllegalArgumentException("Page must be zero or greater");
        if (size < 1 || size > 50) throw new IllegalArgumentException("Size must be between 1 and 50");
        if (minExperience != null && minExperience.signum() < 0)
            throw new IllegalArgumentException("Minimum experience cannot be negative");
        if (maxHourlyRate != null && maxHourlyRate.signum() < 0)
            throw new IllegalArgumentException("Maximum hourly rate cannot be negative");

        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "totalExperienceYears")
                        .and(Sort.by(Sort.Direction.ASC, "createdAt")));

        return ResponseEntity.ok(service.search(
                skill, city, country, minExperience, available,
                maxHourlyRate, currency, pageable));
    }
}
