package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ENGAGEMENT_HISTORY")
public class EngagementHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ENGAGEMENT_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_ENG_HISTORY_ENGAGEMENT"))
    private Engagement engagement;

    @Column(name = "ACTION", nullable = false, length = 50)
    private String action;
    @Column(name = "FROM_STATUS", length = 20)
    private String fromStatus;
    @Column(name = "TO_STATUS", length = 20)
    private String toStatus;
    @Column(name = "ACTOR_USER_ID")
    private Long actorUserId;
    @Column(name = "ACTOR_ROLE", length = 30)
    private String actorRole;
    @Column(name = "ACTOR_NAME", length = 250)
    private String actorName;
    @Column(name = "REASON", length = 1000)
    private String reason;
    @Column(name = "OCCURRED_AT", nullable = false)
    private LocalDateTime occurredAt;

    @PrePersist
    void prePersist() {
        if (occurredAt == null) occurredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Engagement getEngagement() { return engagement; }
    public void setEngagement(Engagement v) { engagement = v; }
    public String getAction() { return action; }
    public void setAction(String v) { action = v; }
    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String v) { fromStatus = v; }
    public String getToStatus() { return toStatus; }
    public void setToStatus(String v) { toStatus = v; }
    public Long getActorUserId() { return actorUserId; }
    public void setActorUserId(Long v) { actorUserId = v; }
    public String getActorRole() { return actorRole; }
    public void setActorRole(String v) { actorRole = v; }
    public String getActorName() { return actorName; }
    public void setActorName(String v) { actorName = v; }
    public String getReason() { return reason; }
    public void setReason(String v) { reason = v; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}
