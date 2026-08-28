package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.WorkLogCreateRequest;
import com.neshtek.expertconnect.dto.WorkLogResponse;
import com.neshtek.expertconnect.dto.WorkLogUpdateRequest;
import com.neshtek.expertconnect.service.WorkLogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/work-logs")
public class WorkLogController {
    private final WorkLogService service;
    public WorkLogController(WorkLogService service){this.service=service;}
    @GetMapping("/engagements/{engagementId}") public Page<WorkLogResponse> byEngagement(@PathVariable Long engagementId,Pageable pageable){return service.byEngagement(engagementId,pageable);}
    @GetMapping("/customers/{customerId}") public Page<WorkLogResponse> byCustomer(@PathVariable Long customerId,Pageable pageable){return service.byCustomer(customerId,pageable);}
    @GetMapping("/experts/{expertId}") public Page<WorkLogResponse> byExpert(@PathVariable Long expertId,Pageable pageable){return service.byExpert(expertId,pageable);}
    @PostMapping("/engagements/{engagementId}") public WorkLogResponse create(@PathVariable Long engagementId,@Valid @RequestBody WorkLogCreateRequest request){return service.create(engagementId,request);}
    @PutMapping("/{id}") public WorkLogResponse update(@PathVariable Long id,@Valid @RequestBody WorkLogUpdateRequest request){return service.update(id,request);}
    @PostMapping("/{id}/submit") public WorkLogResponse submit(@PathVariable Long id){return service.submit(id);}
    @PostMapping("/{id}/approve") public WorkLogResponse approve(@PathVariable Long id,@RequestParam(required=false) String comment){return service.approve(id,comment);}
    @PostMapping("/{id}/reject") public WorkLogResponse reject(@PathVariable Long id,@RequestParam(required=false) String comment){return service.reject(id,comment);}
}
