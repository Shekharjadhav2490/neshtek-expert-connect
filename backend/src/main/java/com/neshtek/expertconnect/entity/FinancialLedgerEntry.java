package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "FINANCIAL_LEDGER_ENTRY")
public class FinancialLedgerEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "LEDGER_ENTRY_ID") private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "CUSTOMER_ID", nullable = false) private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "EXPERT_ID") private Expert expert;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ENGAGEMENT_ID") private Engagement engagement;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "INVOICE_ID") private Invoice invoice;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "PAYMENT_ID") private PaymentTransaction payment;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "SETTLEMENT_ID") private Settlement settlement;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "REPLACEMENT_REQUEST_ID") private ExpertReplacementRequest replacementRequest;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "PARENT_LEDGER_ENTRY_ID") private FinancialLedgerEntry parentEntry;
    @Enumerated(EnumType.STRING) @Column(name = "ENTRY_TYPE", nullable = false, length = 40) private FinancialLedgerEntryType entryType;
    @Enumerated(EnumType.STRING) @Column(name = "ENTRY_DIRECTION", nullable = false, length = 10) private FinancialLedgerDirection direction;
    @Column(name = "AMOUNT", nullable = false, precision = 14, scale = 2) private BigDecimal amount;
    @Column(name = "CURRENCY_CODE", nullable = false, length = 3) private String currencyCode;
    @Column(name = "SOURCE_TYPE", nullable = false, length = 40) private String sourceType;
    @Column(name = "SOURCE_ID", nullable = false, length = 100) private String sourceId;
    @Column(name = "IDEMPOTENCY_KEY", nullable = false, unique = true, length = 180) private String idempotencyKey;
    @Column(name = "DESCRIPTION", nullable = false, length = 2000) private String description;
    @Column(name = "OCCURRED_AT", nullable = false) private LocalDateTime occurredAt;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "CREATED_BY", length = 150) private String createdBy;
    @PrePersist void prePersist() { LocalDateTime n = LocalDateTime.now(); if (occurredAt == null) occurredAt = n; createdAt = n; }
    public Long getId(){return id;} public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;}
    public Expert getExpert(){return expert;} public void setExpert(Expert v){expert=v;} public Engagement getEngagement(){return engagement;} public void setEngagement(Engagement v){engagement=v;}
    public Invoice getInvoice(){return invoice;} public void setInvoice(Invoice v){invoice=v;} public PaymentTransaction getPayment(){return payment;} public void setPayment(PaymentTransaction v){payment=v;}
    public Settlement getSettlement(){return settlement;} public void setSettlement(Settlement v){settlement=v;} public ExpertReplacementRequest getReplacementRequest(){return replacementRequest;} public void setReplacementRequest(ExpertReplacementRequest v){replacementRequest=v;}
    public FinancialLedgerEntry getParentEntry(){return parentEntry;} public void setParentEntry(FinancialLedgerEntry v){parentEntry=v;} public FinancialLedgerEntryType getEntryType(){return entryType;} public void setEntryType(FinancialLedgerEntryType v){entryType=v;}
    public FinancialLedgerDirection getDirection(){return direction;} public void setDirection(FinancialLedgerDirection v){direction=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public String getCurrencyCode(){return currencyCode;} public void setCurrencyCode(String v){currencyCode=v;} public String getSourceType(){return sourceType;} public void setSourceType(String v){sourceType=v;}
    public String getSourceId(){return sourceId;} public void setSourceId(String v){sourceId=v;} public String getIdempotencyKey(){return idempotencyKey;} public void setIdempotencyKey(String v){idempotencyKey=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;} public LocalDateTime getOccurredAt(){return occurredAt;} public void setOccurredAt(LocalDateTime v){occurredAt=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public String getCreatedBy(){return createdBy;} public void setCreatedBy(String v){createdBy=v;}
}
