package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.ExpertRegistrationRequest;
import com.neshtek.expertconnect.dto.ExpertResponse;
import com.neshtek.expertconnect.service.ExpertService;
import jakarta.validation.Valid;
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
}
