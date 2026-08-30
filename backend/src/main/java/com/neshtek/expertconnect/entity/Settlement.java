package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SETTLEMENT")
public class Settlement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SETTLEMENT_ID") private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ENGAGEMENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SETTLEMENT_ENGAGEMENT"))
    private Engagement engagement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EXPERT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SETTLEMENT_EXPERT"))
    private Expert expert;

    @Column(name = "APPROVED_HOURS", nullable = false, precision = 10, scale = 2) private BigDecimal approvedHours;
    @Column(name = "HOURLY_RATE", nullable = false, precision = 12, scale = 2) private BigDecimal hourlyRate;
    @Column(name = "GROSS_AMOUNT", nullable = false, precision = 14, scale = 2) private BigDecimal grossAmount;
    @Column(name = "CURRENCY_CODE", nullable = false, columnDefinition = "CHAR(3)") private String currencyCode;
    @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 30) private SettlementStatus status = SettlementStatus.REQUESTED;
    @Column(name = "REQUESTED_AT", nullable = false) private LocalDateTime requestedAt;
    @Column(name = "CUSTOMER_APPROVED_AT") private LocalDateTime customerApprovedAt;
    @Column(name = "CUSTOMER_REJECTED_AT") private LocalDateTime customerRejectedAt;
    @Column(name = "CUSTOMER_COMMENT", length = 1000) private String customerComment;
    @Column(name = "APPROVED_AT") private LocalDateTime approvedAt;
    @Column(name = "PAID_AT") private LocalDateTime paidAt;
    @Column(name = "REJECTED_AT") private LocalDateTime rejectedAt;
    @Column(name = "ADMIN_COMMENT", length = 1000) private String adminComment;
    @Column(name = "PAYMENT_REFERENCE", length = 200) private String paymentReference;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT", nullable = false) private LocalDateTime updatedAt;

    @PrePersist void prePersist() { LocalDateTime now=LocalDateTime.now(); requestedAt=now; createdAt=now; updatedAt=now; }
    @PreUpdate void preUpdate() { updatedAt=LocalDateTime.now(); }

    public Long getId(){return id;}
    public Engagement getEngagement(){return engagement;} public void setEngagement(Engagement v){engagement=v;}
    public Expert getExpert(){return expert;} public void setExpert(Expert v){expert=v;}
    public BigDecimal getApprovedHours(){return approvedHours;} public void setApprovedHours(BigDecimal v){approvedHours=v;}
    public BigDecimal getHourlyRate(){return hourlyRate;} public void setHourlyRate(BigDecimal v){hourlyRate=v;}
    public BigDecimal getGrossAmount(){return grossAmount;} public void setGrossAmount(BigDecimal v){grossAmount=v;}
    public String getCurrencyCode(){return currencyCode;} public void setCurrencyCode(String v){currencyCode=v;}
    public SettlementStatus getStatus(){return status;} public void setStatus(SettlementStatus v){status=v;}
    public LocalDateTime getRequestedAt(){return requestedAt;}
    public LocalDateTime getCustomerApprovedAt(){return customerApprovedAt;} public void setCustomerApprovedAt(LocalDateTime v){customerApprovedAt=v;}
    public LocalDateTime getCustomerRejectedAt(){return customerRejectedAt;} public void setCustomerRejectedAt(LocalDateTime v){customerRejectedAt=v;}
    public String getCustomerComment(){return customerComment;} public void setCustomerComment(String v){customerComment=v;}
    public LocalDateTime getApprovedAt(){return approvedAt;} public void setApprovedAt(LocalDateTime v){approvedAt=v;}
    public LocalDateTime getPaidAt(){return paidAt;} public void setPaidAt(LocalDateTime v){paidAt=v;}
    public LocalDateTime getRejectedAt(){return rejectedAt;} public void setRejectedAt(LocalDateTime v){rejectedAt=v;}
    public String getAdminComment(){return adminComment;} public void setAdminComment(String v){adminComment=v;}
    public String getPaymentReference(){return paymentReference;} public void setPaymentReference(String v){paymentReference=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
