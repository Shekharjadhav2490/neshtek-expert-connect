package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.InvoiceResponse;
import com.neshtek.expertconnect.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
 @GetMapping("/{id}/pdf") public ResponseEntity<byte[]> pdf(@PathVariable Long id){String fileName=service.pdfFileName(id);byte[] bytes=service.pdf(id);HttpHeaders headers=new HttpHeaders();headers.setContentType(MediaType.APPLICATION_PDF);headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());headers.setContentLength(bytes.length);return ResponseEntity.ok().headers(headers).body(bytes);}
 @PostMapping("/engagements/{engagementId}/generate") @PreAuthorize("hasRole('ADMIN')") public InvoiceResponse generate(@PathVariable Long engagementId){return service.generate(engagementId);}
 @PostMapping("/{id}/issue") @PreAuthorize("hasRole('ADMIN')") public InvoiceResponse issue(@PathVariable Long id){return service.issue(id);}
 @PostMapping("/{id}/cancel") @PreAuthorize("hasRole('ADMIN')") public InvoiceResponse cancel(@PathVariable Long id){return service.cancel(id);}
}
