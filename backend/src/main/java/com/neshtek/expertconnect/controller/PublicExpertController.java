package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.PublicExpertProfileResponse;
import com.neshtek.expertconnect.service.PublicExpertProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/experts")
public class PublicExpertController {
    private final PublicExpertProfileService service;

    public PublicExpertController(PublicExpertProfileService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public PublicExpertProfileResponse getProfile(@PathVariable Long id) {
        return service.getActiveProfile(id);
    }
}
