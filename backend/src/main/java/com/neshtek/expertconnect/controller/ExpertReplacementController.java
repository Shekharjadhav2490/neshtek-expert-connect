package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.ExpertMatchResponse;
import com.neshtek.expertconnect.dto.ExpertReplacementRequestDto;
import com.neshtek.expertconnect.dto.ExpertReplacementResponse;
import com.neshtek.expertconnect.service.ExpertReplacementService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expert-replacements")
public class ExpertReplacementController {
    private final ExpertReplacementService service;
    public ExpertReplacementController(ExpertReplacementService service){this.service=service;}

    @PostMapping("/engagements/{engagementId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ExpertReplacementResponse request(@PathVariable Long engagementId, @Valid @RequestBody ExpertReplacementRequestDto request){return service.request(engagementId, request);}

    @GetMapping("/engagements/{engagementId}")
    public List<ExpertReplacementResponse> byEngagement(@PathVariable Long engagementId){return service.byEngagement(engagementId);}

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ExpertReplacementResponse> pending(){return service.pending();}

    @GetMapping("/{id}/matches")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ExpertMatchResponse> matches(@PathVariable Long id,@RequestParam(defaultValue="5") int limit){return service.matches(id,limit);}

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ExpertReplacementResponse approve(@PathVariable Long id,@RequestParam(required=false) String comment){return service.approve(id,comment);}

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ExpertReplacementResponse assign(@PathVariable Long id,@RequestParam Long newExpertId){return service.assign(id,newExpertId);}

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ExpertReplacementResponse reject(@PathVariable Long id,@RequestParam String reason){return service.reject(id,reason);}

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ExpertReplacementResponse cancel(@PathVariable Long id){return service.cancel(id);}
}
