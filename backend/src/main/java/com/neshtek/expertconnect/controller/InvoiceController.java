package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.InvoiceResponse;
import com.neshtek.expertconnect.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {
 private final InvoiceService service;
 public InvoiceController(InvoiceService service){this.service=service;}
 @GetMapping @PreAuthorize("hasRole('ADMIN')") public Page<InvoiceResponse> all(Pageable pageable){return service.all(pageable);}
 @GetMapping("/customers/{customerId}") @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')") public Page<InvoiceResponse> byCustomer(@PathVariable Long customerId,Pageable pageable){return service.byCustomer(customerId,pageable);}
 @GetMapping("/{id}") public InvoiceResponse get(@PathVariable Long id){return service.get(id);}
 @PostMapping("/engagements/{engagementId}/generate") @PreAuthorize("hasRole('ADMIN')") public InvoiceResponse generate(@PathVariable Long engagementId){return service.generate(engagementId);}
 @PostMapping("/{id}/issue") @PreAuthorize("hasRole('ADMIN')") public InvoiceResponse issue(@PathVariable Long id){return service.issue(id);}
 @PostMapping("/{id}/cancel") @PreAuthorize("hasRole('ADMIN')") public InvoiceResponse cancel(@PathVariable Long id){return service.cancel(id);}
}
