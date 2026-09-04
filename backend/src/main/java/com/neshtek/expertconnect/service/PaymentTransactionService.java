package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.PaymentRequest;
import com.neshtek.expertconnect.dto.PaymentResponse;
import com.neshtek.expertconnect.entity.Invoice;
import com.neshtek.expertconnect.entity.InvoiceStatus;
import com.neshtek.expertconnect.entity.PaymentStatus;
import com.neshtek.expertconnect.entity.PaymentTransaction;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.InvoiceRepository;
import com.neshtek.expertconnect.repository.PaymentTransactionRepository;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PaymentTransactionService {
 private final PaymentTransactionRepository payments; private final InvoiceRepository invoices; private final ResourceAuthorizationService authorization; private final FinancialLedgerService ledger;
 public PaymentTransactionService(PaymentTransactionRepository payments,InvoiceRepository invoices,ResourceAuthorizationService authorization,FinancialLedgerService ledger){this.payments=payments;this.invoices=invoices;this.authorization=authorization;this.ledger=ledger;}
 @Transactional(readOnly=true) public Page<PaymentResponse> byInvoice(Long invoiceId,Pageable pageable){Invoice i=findInvoice(invoiceId);authorization.assertCanAccess(i.getEngagement());return payments.findByInvoiceId(invoiceId,pageable).map(PaymentResponse::from);}
 @Transactional public PaymentResponse record(Long invoiceId,PaymentRequest request){if(!authorization.isAdmin())throw new AccessDeniedException("Only Admin can record payments");Invoice invoice=findInvoice(invoiceId);if(invoice.getStatus()==InvoiceStatus.DRAFT||invoice.getStatus()==InvoiceStatus.CANCELLED||invoice.getStatus()==InvoiceStatus.REFUNDED)throw new IllegalStateException("Payments cannot be recorded for invoice status "+invoice.getStatus());if(payments.existsByPaymentReference(request.paymentReference()))throw new IllegalStateException("Payment reference already exists: "+request.paymentReference());BigDecimal balance=invoice.getTotalAmount().subtract(invoice.getPaidAmount());if(request.amount().compareTo(balance)>0)throw new IllegalArgumentException("Payment amount exceeds invoice balance of "+balance+" "+invoice.getCurrencyCode());PaymentTransaction p=new PaymentTransaction();p.setInvoice(invoice);p.setPaymentReference(request.paymentReference());p.setPaymentDate(request.paymentDate()==null?LocalDate.now():request.paymentDate());p.setAmount(request.amount());p.setCurrencyCode(invoice.getCurrencyCode());p.setPaymentMethod(request.paymentMethod());p.setStatus(PaymentStatus.SUCCESS);p.setNotes(request.notes());PaymentTransaction saved=payments.save(p);BigDecimal newPaid=invoice.getPaidAmount().add(request.amount());invoice.setPaidAmount(newPaid);if(newPaid.compareTo(invoice.getTotalAmount())==0){invoice.setStatus(InvoiceStatus.PAID);invoice.setPaidAt(LocalDateTime.now());}else{invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);invoice.setPaidAt(null);}invoices.save(invoice);ledger.postCustomerPayment(saved);return PaymentResponse.from(saved);}
 @Transactional public PaymentResponse refund(Long paymentId){if(!authorization.isAdmin())throw new AccessDeniedException("Only Admin can refund payments");PaymentTransaction p=payments.findById(paymentId).orElseThrow(()->new ResourceNotFoundException("Payment not found: "+paymentId));if(p.getStatus()!=PaymentStatus.SUCCESS)throw new IllegalStateException("Only successful payments can be refunded");Invoice invoice=p.getInvoice();p.setStatus(PaymentStatus.REFUNDED);invoice.setPaidAmount(invoice.getPaidAmount().subtract(p.getAmount()));invoice.setPaidAt(null);if(invoice.getPaidAmount().compareTo(BigDecimal.ZERO)==0)invoice.setStatus(InvoiceStatus.ISSUED);else invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);invoices.save(invoice);PaymentTransaction saved=payments.save(p);ledger.postPaymentRefund(saved);return PaymentResponse.from(saved);}
 private Invoice findInvoice(Long id){return invoices.findById(id).orElseThrow(()->new ResourceNotFoundException("Invoice not found: "+id));}
}
