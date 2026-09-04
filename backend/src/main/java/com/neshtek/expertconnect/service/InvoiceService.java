package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.InvoiceResponse;
import com.neshtek.expertconnect.entity.Invoice;
import com.neshtek.expertconnect.entity.InvoiceStatus;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.EngagementRepository;
import com.neshtek.expertconnect.repository.InvoiceRepository;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {
 private final InvoiceRepository invoices; private final EngagementRepository engagements; private final BillingSummaryService billing; private final ResourceAuthorizationService authorization; private final InvoicePdfService pdfService; private final FinancialLedgerService ledger;
 public InvoiceService(InvoiceRepository invoices,EngagementRepository engagements,BillingSummaryService billing,ResourceAuthorizationService authorization,InvoicePdfService pdfService,FinancialLedgerService ledger){this.invoices=invoices;this.engagements=engagements;this.billing=billing;this.authorization=authorization;this.pdfService=pdfService;this.ledger=ledger;}
 @Transactional(readOnly=true) public Page<InvoiceResponse> all(Pageable p){return invoices.findAll(p).map(InvoiceResponse::from);}
 @Transactional(readOnly=true) public Page<InvoiceResponse> byCustomer(Long customerId,Pageable p){authorization.assertCustomerOwns(customerId);return invoices.findByCustomerId(customerId,p).map(InvoiceResponse::from);}
 @Transactional(readOnly=true) public InvoiceResponse get(Long id){Invoice i=find(id);authorization.assertCanAccess(i.getEngagement());return InvoiceResponse.from(i);}
 @Transactional public InvoiceResponse generate(Long engagementId){if(!authorization.isAdmin()) throw new org.springframework.security.access.AccessDeniedException("Only Admin can generate invoices");var e=engagements.findWithDetailsById(engagementId).orElseThrow(()->new ResourceNotFoundException("Engagement not found: "+engagementId));if(invoices.existsByEngagementId(engagementId)) throw new IllegalStateException("An invoice already exists for engagement "+engagementId);var s=billing.get(engagementId);BigDecimal subtotal=s.approvedBilling()==null?BigDecimal.ZERO:s.approvedBilling();Invoice i=new Invoice();i.setInvoiceNumber("INV-"+LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)+"-"+engagementId);i.setCustomer(e.getCustomer());i.setEngagement(e);i.setInvoiceDate(LocalDate.now());i.setDueDate(LocalDate.now().plusDays(30));i.setCurrencyCode(e.getConsultationRequest().getCurrencyCode());i.setSubtotalAmount(subtotal);i.setTaxAmount(BigDecimal.ZERO);i.setTotalAmount(subtotal);i.setPaidAmount(BigDecimal.ZERO);i.setStatus(InvoiceStatus.DRAFT);i.setNotes("Generated from approved work for engagement #"+engagementId);return InvoiceResponse.from(invoices.save(i));}
 @Transactional public InvoiceResponse issue(Long id){if(!authorization.isAdmin())throw new org.springframework.security.access.AccessDeniedException("Only Admin can issue invoices");Invoice i=find(id);if(i.getStatus()!=InvoiceStatus.DRAFT)throw new IllegalStateException("Only DRAFT invoices can be issued");i.setStatus(InvoiceStatus.ISSUED);i.setIssuedAt(LocalDateTime.now());generateAndStorePdf(i);Invoice saved=invoices.save(i);ledger.postInvoiceIssued(saved);return InvoiceResponse.from(saved);}
 @Transactional public InvoiceResponse cancel(Long id){if(!authorization.isAdmin())throw new org.springframework.security.access.AccessDeniedException("Only Admin can cancel invoices");Invoice i=find(id);if(i.getStatus()==InvoiceStatus.PAID||i.getStatus()==InvoiceStatus.REFUNDED)throw new IllegalStateException("Paid or refunded invoices cannot be cancelled");i.setStatus(InvoiceStatus.CANCELLED);i.setCancelledAt(LocalDateTime.now());return InvoiceResponse.from(invoices.save(i));}
 @Transactional public byte[] pdf(Long id){Invoice i=find(id);authorization.assertCanAccess(i.getEngagement());if(i.getStatus()==InvoiceStatus.DRAFT)throw new IllegalStateException("Invoice PDF is available after the invoice is issued");if(i.getPdfContent()==null||i.getPdfContent().length==0){generateAndStorePdf(i);}return i.getPdfContent();}
 @Transactional(readOnly=true) public String pdfFileName(Long id){Invoice i=find(id);authorization.assertCanAccess(i.getEngagement());return i.getPdfFileName()==null?i.getInvoiceNumber()+".pdf":i.getPdfFileName();}
 private void generateAndStorePdf(Invoice i){byte[] bytes=pdfService.generate(i);i.setPdfContent(bytes);i.setPdfFileName(i.getInvoiceNumber()+".pdf");i.setPdfContentType("application/pdf");i.setPdfGeneratedAt(LocalDateTime.now());}
 private Invoice find(Long id){return invoices.findById(id).orElseThrow(()->new ResourceNotFoundException("Invoice not found: "+id));}
}
