package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EXPERT_VERIFICATION")
public class ExpertVerification {
    @Id
    @Column(name = "EXPERT_ID")
    private Long expertId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "EXPERT_ID")
    private Expert expert;

    @Column(name = "MOBILE_VERIFIED", columnDefinition = "CHAR(1)")
    private String mobileVerified = "N";

    @Column(name = "EMAIL_VERIFIED", columnDefinition = "CHAR(1)")
    private String emailVerified = "N";

    @Column(name = "LINKEDIN_VERIFIED", columnDefinition = "CHAR(1)")
    private String linkedinVerified = "N";

    @Column(name = "IDENTITY_VERIFIED", columnDefinition = "CHAR(1)")
    private String identityVerified = "N";

    @Column(name = "EXPERIENCE_VERIFIED", columnDefinition = "CHAR(1)")
    private String experienceVerified = "N";

    @Column(name = "VERIFIED_AT")
    private LocalDateTime verifiedAt;

    @Column(name = "VERIFIED_BY", length = 150)
    private String verifiedBy;

    public Long getExpertId() { return expertId; }
    public Expert getExpert() { return expert; }
    public void setExpert(Expert value) { expert = value; }
    public String getMobileVerified() { return mobileVerified; }
    public void setMobileVerified(String value) { mobileVerified = value; }
    public String getEmailVerified() { return emailVerified; }
    public void setEmailVerified(String value) { emailVerified = value; }
    public String getLinkedinVerified() { return linkedinVerified; }
    public void setLinkedinVerified(String value) { linkedinVerified = value; }
    public String getIdentityVerified() { return identityVerified; }
    public void setIdentityVerified(String value) { identityVerified = value; }
    public String getExperienceVerified() { return experienceVerified; }
    public void setExperienceVerified(String value) { experienceVerified = value; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime value) { verifiedAt = value; }
    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String value) { verifiedBy = value; }
}
