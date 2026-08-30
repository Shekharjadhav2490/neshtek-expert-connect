package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="INVOICE")
public class Invoice {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="INVOICE_ID") private Long id;
 @Column(name="INVOICE_NUMBER",nullable=false,unique=true,length=40) private String invoiceNumber;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="CUSTOMER_ID",nullable=false) private Customer customer;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="ENGAGEMENT_ID",nullable=false) private Engagement engagement;
 @Column(name="INVOICE_DATE",nullable=false) private LocalDate invoiceDate;
 @Column(name="DUE_DATE") private LocalDate dueDate;
 @Column(name="CURRENCY_CODE",nullable=false,length=3) private String currencyCode;
 @Column(name="SUBTOTAL_AMOUNT",nullable=false,precision=14,scale=2) private BigDecimal subtotalAmount=BigDecimal.ZERO;
 @Column(name="TAX_AMOUNT",nullable=false,precision=14,scale=2) private BigDecimal taxAmount=BigDecimal.ZERO;
 @Column(name="TOTAL_AMOUNT",nullable=false,precision=14,scale=2) private BigDecimal totalAmount=BigDecimal.ZERO;
 @Column(name="PAID_AMOUNT",nullable=false,precision=14,scale=2) private BigDecimal paidAmount=BigDecimal.ZERO;
 @Enumerated(EnumType.STRING) @Column(name="STATUS",nullable=false,length=30) private InvoiceStatus status=InvoiceStatus.DRAFT;
 @Column(name="NOTES",length=2000) private String notes;
 @Column(name="ISSUED_AT") private LocalDateTime issuedAt; @Column(name="PAID_AT") private LocalDateTime paidAt; @Column(name="CANCELLED_AT") private LocalDateTime cancelledAt;
 @Lob @Basic(fetch=FetchType.LAZY) @Column(name="PDF_CONTENT") private byte[] pdfContent;
 @Column(name="PDF_FILE_NAME",length=255) private String pdfFileName;
 @Column(name="PDF_CONTENT_TYPE",length=100) private String pdfContentType;
 @Column(name="PDF_GENERATED_AT") private LocalDateTime pdfGeneratedAt;
 @Column(name="CREATED_AT",nullable=false) private LocalDateTime createdAt; @Column(name="UPDATED_AT",nullable=false) private LocalDateTime updatedAt;
 @PrePersist void prePersist(){LocalDateTime n=LocalDateTime.now();createdAt=n;updatedAt=n;if(invoiceDate==null)invoiceDate=LocalDate.now();}
 @PreUpdate void preUpdate(){updatedAt=LocalDateTime.now();}
 public Long getId(){return id;} public String getInvoiceNumber(){return invoiceNumber;} public void setInvoiceNumber(String v){invoiceNumber=v;} public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;} public Engagement getEngagement(){return engagement;} public void setEngagement(Engagement v){engagement=v;} public LocalDate getInvoiceDate(){return invoiceDate;} public void setInvoiceDate(LocalDate v){invoiceDate=v;} public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate v){dueDate=v;} public String getCurrencyCode(){return currencyCode;} public void setCurrencyCode(String v){currencyCode=v;} public BigDecimal getSubtotalAmount(){return subtotalAmount;} public void setSubtotalAmount(BigDecimal v){subtotalAmount=v;} public BigDecimal getTaxAmount(){return taxAmount;} public void setTaxAmount(BigDecimal v){taxAmount=v;} public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;} public BigDecimal getPaidAmount(){return paidAmount;} public void setPaidAmount(BigDecimal v){paidAmount=v;} public InvoiceStatus getStatus(){return status;} public void setStatus(InvoiceStatus v){status=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public LocalDateTime getIssuedAt(){return issuedAt;} public void setIssuedAt(LocalDateTime v){issuedAt=v;} public LocalDateTime getPaidAt(){return paidAt;} public void setPaidAt(LocalDateTime v){paidAt=v;} public LocalDateTime getCancelledAt(){return cancelledAt;} public void setCancelledAt(LocalDateTime v){cancelledAt=v;} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
 public byte[] getPdfContent(){return pdfContent;} public void setPdfContent(byte[] v){pdfContent=v;} public String getPdfFileName(){return pdfFileName;} public void setPdfFileName(String v){pdfFileName=v;} public String getPdfContentType(){return pdfContentType;} public void setPdfContentType(String v){pdfContentType=v;} public LocalDateTime getPdfGeneratedAt(){return pdfGeneratedAt;} public void setPdfGeneratedAt(LocalDateTime v){pdfGeneratedAt=v;}
}
