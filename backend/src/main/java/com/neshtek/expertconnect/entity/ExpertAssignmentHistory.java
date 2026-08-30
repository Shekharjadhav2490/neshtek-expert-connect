package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EXPERT_ASSIGNMENT_HISTORY")
public class ExpertAssignmentHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ASSIGNMENT_HISTORY_ID") private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "ENGAGEMENT_ID", nullable = false) private Engagement engagement;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "EXPERT_ID", nullable = false) private Expert expert;
    @Column(name = "ACTION", nullable = false, length = 40) private String action;
    @Column(name = "EFFECTIVE_FROM", nullable = false) private LocalDateTime effectiveFrom;
    @Column(name = "EFFECTIVE_TO") private LocalDateTime effectiveTo;
    @Column(name = "REASON", length = 2000) private String reason;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ACTOR_USER_ID") private AppUser actor;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){ LocalDateTime n=LocalDateTime.now(); if(effectiveFrom==null) effectiveFrom=n; createdAt=n; }
    public Long getId(){return id;} public Engagement getEngagement(){return engagement;} public void setEngagement(Engagement v){engagement=v;}
    public Expert getExpert(){return expert;} public void setExpert(Expert v){expert=v;} public String getAction(){return action;} public void setAction(String v){action=v;}
    public LocalDateTime getEffectiveFrom(){return effectiveFrom;} public void setEffectiveFrom(LocalDateTime v){effectiveFrom=v;} public LocalDateTime getEffectiveTo(){return effectiveTo;} public void setEffectiveTo(LocalDateTime v){effectiveTo=v;}
    public String getReason(){return reason;} public void setReason(String v){reason=v;} public AppUser getActor(){return actor;} public void setActor(AppUser v){actor=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
