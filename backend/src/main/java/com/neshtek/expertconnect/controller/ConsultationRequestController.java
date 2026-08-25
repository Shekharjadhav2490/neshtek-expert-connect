package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.ConsultationRejectRequest;
import com.neshtek.expertconnect.dto.ConsultationRequestRequest;
import com.neshtek.expertconnect.dto.ConsultationRequestResponse;
import com.neshtek.expertconnect.service.ConsultationRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/consultation-requests")
public class ConsultationRequestController {
    private final ConsultationRequestService service;
    public ConsultationRequestController(ConsultationRequestService service){this.service=service;}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultationRequestResponse create(@Valid @RequestBody ConsultationRequestRequest request){return service.create(request);}

    @GetMapping("/{id}")
    public ConsultationRequestResponse get(@PathVariable Long id){return service.get(id);}

    @GetMapping("/customers/{customerId}")
    public Page<ConsultationRequestResponse> byCustomer(@PathVariable Long customerId,Pageable pageable){return service.byCustomer(customerId,pageable);}

    @GetMapping("/experts/{expertId}")
    public Page<ConsultationRequestResponse> byExpert(@PathVariable Long expertId,Pageable pageable){return service.byExpert(expertId,pageable);}

    @PostMapping("/{id}/accept")
    public ConsultationRequestResponse accept(@PathVariable Long id){return service.accept(id);}

    @PostMapping("/{id}/reject")
    public ConsultationRequestResponse reject(@PathVariable Long id,@Valid @RequestBody ConsultationRejectRequest request){return service.reject(id,request);}
}
