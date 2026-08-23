package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONSULTATION_REQUEST")
public class ConsultationRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_ID") private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CONSULT_CUSTOMER"))
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUIREMENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CONSULT_REQUIREMENT"))
    private CustomerRequirement requirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXPERT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CONSULT_EXPERT"))
    private Expert expert;

    @Column(name = "MESSAGE", length = 2000) private String message;
    @Column(name = "REQUESTED_START_DATE") private LocalDate requestedStartDate;
    @Column(name = "ESTIMATED_HOURS", precision = 6, scale = 1) private BigDecimal estimatedHours;
    @Column(name = "PROPOSED_RATE", precision = 12, scale = 2) private BigDecimal proposedRate;
    @Column(name = "CURRENCY_CODE", nullable = false, columnDefinition = "CHAR(3)") private String currencyCode = "USD";
    @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 20) private ConsultationRequestStatus status = ConsultationRequestStatus.PENDING;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT", nullable = false) private LocalDateTime updatedAt;

    @PrePersist void prePersist(){ LocalDateTime now=LocalDateTime.now(); createdAt=now; updatedAt=now; }
    @PreUpdate void preUpdate(){ updatedAt=LocalDateTime.now(); }

    public Long getId(){return id;}
    public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;}
    public CustomerRequirement getRequirement(){return requirement;} public void setRequirement(CustomerRequirement v){requirement=v;}
    public Expert getExpert(){return expert;} public void setExpert(Expert v){expert=v;}
    public String getMessage(){return message;} public void setMessage(String v){message=v;}
    public LocalDate getRequestedStartDate(){return requestedStartDate;} public void setRequestedStartDate(LocalDate v){requestedStartDate=v;}
    public BigDecimal getEstimatedHours(){return estimatedHours;} public void setEstimatedHours(BigDecimal v){estimatedHours=v;}
    public BigDecimal getProposedRate(){return proposedRate;} public void setProposedRate(BigDecimal v){proposedRate=v;}
    public String getCurrencyCode(){return currencyCode;} public void setCurrencyCode(String v){currencyCode=v;}
    public ConsultationRequestStatus getStatus(){return status;} public void setStatus(ConsultationRequestStatus v){status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
