package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EXPERT_EXPERTISE")
public class ExpertExpertise {
    @Id
    @Column(name = "EXPERT_ID")
    private Long expertId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "EXPERT_ID")
    private Expert expert;

    @Lob
    @Column(name = "TECHNICAL_EXPERTISE", nullable = false, columnDefinition = "CLOB")
    private String technicalExpertise;

    @Column(name = "WORD_COUNT", nullable = false)
    private Integer wordCount;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() { LocalDateTime now = LocalDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate
    void preUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getExpertId() { return expertId; }
    public Expert getExpert() { return expert; }
    public void setExpert(Expert v) { expert = v; }
    public String getTechnicalExpertise() { return technicalExpertise; }
    public void setTechnicalExpertise(String v) { technicalExpertise = v; }
    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer v) { wordCount = v; }
}
