package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.CustomerRequirementRequest;
import com.neshtek.expertconnect.dto.CustomerRequirementResponse;
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
    public CustomerRequirementController(CustomerRequirementService service){this.service=service;}

    @PostMapping
    public ResponseEntity<CustomerRequirementResponse> create(@Valid @RequestBody CustomerRequirementRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public CustomerRequirementResponse get(@PathVariable Long id){return service.get(id);}

    @GetMapping
    public Page<CustomerRequirementResponse> list(@RequestParam(defaultValue="0") int page,
                                                  @RequestParam(defaultValue="20") int size){
        int safeSize=Math.min(Math.max(size,1),100);
        return service.list(PageRequest.of(Math.max(page,0),safeSize,Sort.by(Sort.Direction.DESC,"createdAt")));
    }

    @PutMapping("/{id}")
    public CustomerRequirementResponse update(@PathVariable Long id,
                                              @Valid @RequestBody CustomerRequirementRequest request){
        return service.update(id, request);
    }
}
