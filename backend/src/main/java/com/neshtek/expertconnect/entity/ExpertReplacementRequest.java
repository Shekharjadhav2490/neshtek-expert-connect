package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EXPERT_REPLACEMENT_REQUEST")
public class ExpertReplacementRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPLACEMENT_REQUEST_ID") private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ENGAGEMENT_ID", nullable = false) private Engagement engagement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CURRENT_EXPERT_ID", nullable = false) private Expert currentExpert;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUESTED_BY_USER_ID", nullable = false) private AppUser requestedBy;

    @Enumerated(EnumType.STRING) @Column(name = "REASON_CODE", nullable = false, length = 40)
    private ExpertReplacementReason reasonCode;
    @Column(name = "COMMENTS", nullable = false, length = 2000) private String comments;
    @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 30)
    private ExpertReplacementStatus status = ExpertReplacementStatus.REQUESTED;
    @Column(name = "WORK_CUTOFF_AT") private LocalDateTime workCutoffAt;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "REVIEWED_BY_USER_ID") private AppUser reviewedBy;
    @Column(name = "REVIEWER_COMMENT", length = 2000) private String reviewerComment;
    @Column(name = "REQUESTED_AT", nullable = false) private LocalDateTime requestedAt;
    @Column(name = "REVIEWED_AT") private LocalDateTime reviewedAt;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT", nullable = false) private LocalDateTime updatedAt;

    @PrePersist void prePersist(){ LocalDateTime n=LocalDateTime.now(); requestedAt=n; createdAt=n; updatedAt=n; }
    @PreUpdate void preUpdate(){ updatedAt=LocalDateTime.now(); }

    public Long getId(){return id;}
    public Engagement getEngagement(){return engagement;} public void setEngagement(Engagement v){engagement=v;}
    public Expert getCurrentExpert(){return currentExpert;} public void setCurrentExpert(Expert v){currentExpert=v;}
    public AppUser getRequestedBy(){return requestedBy;} public void setRequestedBy(AppUser v){requestedBy=v;}
    public ExpertReplacementReason getReasonCode(){return reasonCode;} public void setReasonCode(ExpertReplacementReason v){reasonCode=v;}
    public String getComments(){return comments;} public void setComments(String v){comments=v;}
    public ExpertReplacementStatus getStatus(){return status;} public void setStatus(ExpertReplacementStatus v){status=v;}
    public LocalDateTime getWorkCutoffAt(){return workCutoffAt;} public void setWorkCutoffAt(LocalDateTime v){workCutoffAt=v;}
    public AppUser getReviewedBy(){return reviewedBy;} public void setReviewedBy(AppUser v){reviewedBy=v;}
    public String getReviewerComment(){return reviewerComment;} public void setReviewerComment(String v){reviewerComment=v;}
    public LocalDateTime getRequestedAt(){return requestedAt;}
    public LocalDateTime getReviewedAt(){return reviewedAt;} public void setReviewedAt(LocalDateTime v){reviewedAt=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
