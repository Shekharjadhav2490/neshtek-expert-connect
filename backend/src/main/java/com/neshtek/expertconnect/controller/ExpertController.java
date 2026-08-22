package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.ExpertRegistrationRequest;
import com.neshtek.expertconnect.dto.ExpertResponse;
import com.neshtek.expertconnect.service.ExpertService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/experts")
public class ExpertController {
    private final ExpertService service;

    public ExpertController(ExpertService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ExpertResponse> register(@Valid @RequestBody ExpertRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }

    @GetMapping("/{id}")
    public ExpertResponse get(@PathVariable Long id) { return service.get(id); }

    @GetMapping
    public Page<ExpertResponse> search(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0) throw new IllegalArgumentException("Page must be zero or greater");
        if (size < 1 || size > 100) throw new IllegalArgumentException("Size must be between 1 and 100");
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return service.search(skill, status, city, pageable);
    }
}
