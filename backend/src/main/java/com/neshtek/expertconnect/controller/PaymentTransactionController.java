package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.PaymentRequest;
import com.neshtek.expertconnect.dto.PaymentResponse;
import com.neshtek.expertconnect.service.PaymentTransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentTransactionController {
 private final PaymentTransactionService service;
 public PaymentTransactionController(PaymentTransactionService service){this.service=service;}
 @GetMapping("/invoices/{invoiceId}") @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')") public Page<PaymentResponse> byInvoice(@PathVariable Long invoiceId,Pageable pageable){return service.byInvoice(invoiceId,pageable);}
 @PostMapping("/invoices/{invoiceId}") @PreAuthorize("hasRole('ADMIN')") public PaymentResponse record(@PathVariable Long invoiceId,@Valid @RequestBody PaymentRequest request){return service.record(invoiceId,request);}
 @PostMapping("/{paymentId}/refund") @PreAuthorize("hasRole('ADMIN')") public PaymentResponse refund(@PathVariable Long paymentId){return service.refund(paymentId);}
}
