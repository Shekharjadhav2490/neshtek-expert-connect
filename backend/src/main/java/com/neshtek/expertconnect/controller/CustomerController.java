package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.CustomerRequest;
import com.neshtek.expertconnect.dto.CustomerResponse;
import com.neshtek.expertconnect.dto.CustomerRequirementResponse;
import com.neshtek.expertconnect.service.CustomerRequirementService;
import com.neshtek.expertconnect.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService service;
    private final CustomerRequirementService requirementService;
    public CustomerController(CustomerService service, CustomerRequirementService requirementService) { this.service=service; this.requirementService=requirementService; }
    @PostMapping public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
    @GetMapping("/{id}") public CustomerResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping("/{id}/requirements") public Page<CustomerRequirementResponse> requirements(@PathVariable Long id,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { if(page<0) throw new IllegalArgumentException("Page must be zero or greater"); if(size<1||size>100) throw new IllegalArgumentException("Size must be between 1 and 100"); return requirementService.listByCustomer(id,PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt"))); }
    @GetMapping public Page<CustomerResponse> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { if(page<0) throw new IllegalArgumentException("Page must be zero or greater"); if(size<1||size>100) throw new IllegalArgumentException("Size must be between 1 and 100"); return service.list(PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt"))); }
    @PutMapping("/{id}") public CustomerResponse update(@PathVariable Long id,@Valid @RequestBody CustomerRequest request) { return service.update(id,request); }
}
