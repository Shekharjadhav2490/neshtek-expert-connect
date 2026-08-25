package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.CustomerRequirementRequest;
import com.neshtek.expertconnect.dto.CustomerRequirementResponse;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import com.neshtek.expertconnect.service.CustomerRequirementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/requirements")
public class CustomerRequirementController {
    private final CustomerRequirementService service;
    private final ResourceAuthorizationService authorization;
    public CustomerRequirementController(CustomerRequirementService service, ResourceAuthorizationService authorization){this.service=service;this.authorization=authorization;}

    @PostMapping
    public ResponseEntity<CustomerRequirementResponse> create(@Valid @RequestBody CustomerRequirementRequest request){
        if (request.customerId() == null) throw new IllegalArgumentException("customerId is required");
        authorization.assertCanCreateForCustomer(request.customerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public CustomerRequirementResponse get(@PathVariable Long id){
        CustomerRequirementResponse response=service.get(id);
        authorization.assertCustomerOwns(response.customerId());
        return response;
    }

    @GetMapping
    public Page<CustomerRequirementResponse> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){
        if (!authorization.isAdmin()) throw new org.springframework.security.access.AccessDeniedException("Admin access is required");
        int safeSize=Math.min(Math.max(size,1),100);
        return service.list(PageRequest.of(Math.max(page,0),safeSize,Sort.by(Sort.Direction.DESC,"createdAt")));
    }

    @PutMapping("/{id}")
    public CustomerRequirementResponse update(@PathVariable Long id,@Valid @RequestBody CustomerRequirementRequest request){
        CustomerRequirementResponse existing=service.get(id);
        authorization.assertCustomerOwns(existing.customerId());
        if(request.customerId()!=null) authorization.assertCustomerOwns(request.customerId());
        return service.update(id,request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        CustomerRequirementResponse existing=service.get(id);
        authorization.assertCustomerOwns(existing.customerId());
        service.delete(id);
    }
}
