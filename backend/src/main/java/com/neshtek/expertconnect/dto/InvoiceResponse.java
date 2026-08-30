package com.neshtek.expertconnect.dto;

import com.neshtek.expertconnect.entity.Invoice;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InvoiceResponse(Long id,String invoiceNumber,Long customerId,String companyName,Long engagementId,String requirementTitle,Long expertId,String expertName,LocalDate invoiceDate,LocalDate dueDate,String currencyCode,BigDecimal subtotalAmount,BigDecimal taxAmount,BigDecimal totalAmount,BigDecimal paidAmount,BigDecimal balanceDue,String status,String notes,LocalDateTime issuedAt,LocalDateTime paidAt) {
 public static InvoiceResponse from(Invoice i){
  var e=i.getEngagement(); var expert=e.getExpert();
  return new InvoiceResponse(i.getId(),i.getInvoiceNumber(),i.getCustomer().getId(),i.getCustomer().getCompanyName(),e.getId(),e.getRequirement().getTitle(),expert.getId(),expert.getFirstName()+" "+expert.getLastName(),i.getInvoiceDate(),i.getDueDate(),i.getCurrencyCode(),i.getSubtotalAmount(),i.getTaxAmount(),i.getTotalAmount(),i.getPaidAmount(),i.getTotalAmount().subtract(i.getPaidAmount()),i.getStatus().name(),i.getNotes(),i.getIssuedAt(),i.getPaidAt());
 }
}
