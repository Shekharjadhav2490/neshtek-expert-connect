package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ENGAGEMENT")
public class Engagement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "ENGAGEMENT_ID") private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "CONSULTATION_REQUEST_ID", nullable = false, unique = true, foreignKey = @ForeignKey(name = "FK_ENGAGEMENT_CONSULT")) private ConsultationRequest consultationRequest;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "CUSTOMER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ENGAGEMENT_CUSTOMER")) private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "EXPERT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ENGAGEMENT_EXPERT")) private Expert expert;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "REQUIREMENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ENGAGEMENT_REQUIREMENT")) private CustomerRequirement requirement;
    @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 20) private EngagementStatus status = EngagementStatus.READY;
    @Column(name = "STARTED_AT") private LocalDateTime startedAt;
    @Column(name = "PAUSED_AT") private LocalDateTime pausedAt;
    @Column(name = "RESUMED_AT") private LocalDateTime resumedAt;
    @Column(name = "PAUSE_REASON", length = 1000) private String pauseReason;
    @Column(name = "COMPLETED_AT") private LocalDateTime completedAt;
    @Column(name = "CANCELLED_AT") private LocalDateTime cancelledAt;
    @Column(name = "REPLACEMENT_OF_ENGAGEMENT_ID") private Long replacementOfEngagementId;
    @Column(name = "REPLACED_BY_ENGAGEMENT_ID") private Long replacedByEngagementId;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT", nullable = false) private LocalDateTime updatedAt;
    @PrePersist void prePersist(){LocalDateTime now=LocalDateTime.now();createdAt=now;updatedAt=now;}
    @PreUpdate void preUpdate(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public ConsultationRequest getConsultationRequest(){return consultationRequest;} public void setConsultationRequest(ConsultationRequest v){consultationRequest=v;}
    public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;} public Expert getExpert(){return expert;} public void setExpert(Expert v){expert=v;}
    public CustomerRequirement getRequirement(){return requirement;} public void setRequirement(CustomerRequirement v){requirement=v;} public EngagementStatus getStatus(){return status;} public void setStatus(EngagementStatus v){status=v;}
    public LocalDateTime getStartedAt(){return startedAt;} public void setStartedAt(LocalDateTime v){startedAt=v;} public LocalDateTime getPausedAt(){return pausedAt;} public void setPausedAt(LocalDateTime v){pausedAt=v;}
    public LocalDateTime getResumedAt(){return resumedAt;} public void setResumedAt(LocalDateTime v){resumedAt=v;} public String getPauseReason(){return pauseReason;} public void setPauseReason(String v){pauseReason=v;}
    public LocalDateTime getCompletedAt(){return completedAt;} public void setCompletedAt(LocalDateTime v){completedAt=v;} public LocalDateTime getCancelledAt(){return cancelledAt;} public void setCancelledAt(LocalDateTime v){cancelledAt=v;}
    public Long getReplacementOfEngagementId(){return replacementOfEngagementId;} public void setReplacementOfEngagementId(Long v){replacementOfEngagementId=v;}
    public Long getReplacedByEngagementId(){return replacedByEngagementId;} public void setReplacedByEngagementId(Long v){replacedByEngagementId=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
