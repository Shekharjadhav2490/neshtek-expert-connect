package com.neshtek.expertconnect.dto;

import com.neshtek.expertconnect.entity.PaymentTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(Long id,Long invoiceId,String invoiceNumber,String paymentReference,LocalDate paymentDate,BigDecimal amount,String currencyCode,String paymentMethod,String status,String notes){
 public static PaymentResponse from(PaymentTransaction p){return new PaymentResponse(p.getId(),p.getInvoice().getId(),p.getInvoice().getInvoiceNumber(),p.getPaymentReference(),p.getPaymentDate(),p.getAmount(),p.getCurrencyCode(),p.getPaymentMethod().name(),p.getStatus().name(),p.getNotes());}
}
