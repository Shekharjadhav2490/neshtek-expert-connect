package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="PAYMENT_TRANSACTION")
public class PaymentTransaction {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="PAYMENT_ID") private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="INVOICE_ID",nullable=false) private Invoice invoice;
 @Column(name="PAYMENT_REFERENCE",nullable=false,unique=true,length=100) private String paymentReference;
 @Column(name="PAYMENT_DATE",nullable=false) private LocalDate paymentDate;
 @Column(name="AMOUNT",nullable=false,precision=14,scale=2) private BigDecimal amount;
 @Column(name="CURRENCY_CODE",nullable=false,length=3) private String currencyCode;
 @Enumerated(EnumType.STRING) @Column(name="PAYMENT_METHOD",nullable=false,length=30) private PaymentMethod paymentMethod;
 @Enumerated(EnumType.STRING) @Column(name="STATUS",nullable=false,length=30) private PaymentStatus status=PaymentStatus.SUCCESS;
 @Column(name="NOTES",length=1000) private String notes;
 @Column(name="CREATED_AT",nullable=false) private LocalDateTime createdAt;
 @Column(name="UPDATED_AT",nullable=false) private LocalDateTime updatedAt;
 @PrePersist void prePersist(){LocalDateTime n=LocalDateTime.now();createdAt=n;updatedAt=n;if(paymentDate==null)paymentDate=LocalDate.now();}
 @PreUpdate void preUpdate(){updatedAt=LocalDateTime.now();}
 public Long getId(){return id;} public Invoice getInvoice(){return invoice;} public void setInvoice(Invoice v){invoice=v;} public String getPaymentReference(){return paymentReference;} public void setPaymentReference(String v){paymentReference=v;} public LocalDate getPaymentDate(){return paymentDate;} public void setPaymentDate(LocalDate v){paymentDate=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;} public String getCurrencyCode(){return currencyCode;} public void setCurrencyCode(String v){currencyCode=v;} public PaymentMethod getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(PaymentMethod v){paymentMethod=v;} public PaymentStatus getStatus(){return status;} public void setStatus(PaymentStatus v){status=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
