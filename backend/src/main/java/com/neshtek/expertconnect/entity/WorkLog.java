package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "WORK_LOG")
public class WorkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORK_LOG_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ENGAGEMENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_WORK_LOG_ENGAGEMENT"))
    private Engagement engagement;

    @Column(name = "WORK_DATE", nullable = false)
    private LocalDate workDate;

    @Column(name = "HOURS", nullable = false, precision = 8, scale = 2)
    private BigDecimal hours;

    @Column(name = "DESCRIPTION", nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private WorkLogStatus status = WorkLogStatus.DRAFT;

    @Column(name = "SUBMITTED_AT") private LocalDateTime submittedAt;
    @Column(name = "REVIEWED_AT") private LocalDateTime reviewedAt;
    @Column(name = "REVIEWER_COMMENT", length = 1000) private String reviewerComment;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT", nullable = false) private LocalDateTime updatedAt;

    @PrePersist void prePersist() { LocalDateTime now=LocalDateTime.now(); createdAt=now; updatedAt=now; }
    @PreUpdate void preUpdate() { updatedAt=LocalDateTime.now(); }

    public Long getId(){return id;}
    public Engagement getEngagement(){return engagement;} public void setEngagement(Engagement v){engagement=v;}
    public LocalDate getWorkDate(){return workDate;} public void setWorkDate(LocalDate v){workDate=v;}
    public BigDecimal getHours(){return hours;} public void setHours(BigDecimal v){hours=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public WorkLogStatus getStatus(){return status;} public void setStatus(WorkLogStatus v){status=v;}
    public LocalDateTime getSubmittedAt(){return submittedAt;} public void setSubmittedAt(LocalDateTime v){submittedAt=v;}
    public LocalDateTime getReviewedAt(){return reviewedAt;} public void setReviewedAt(LocalDateTime v){reviewedAt=v;}
    public String getReviewerComment(){return reviewerComment;} public void setReviewerComment(String v){reviewerComment=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
