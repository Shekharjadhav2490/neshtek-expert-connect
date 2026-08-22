package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EXPERT")
public class Expert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "EXPERT_ID") private Long id;
    @Column(name = "FIRST_NAME", nullable = false, length = 100) private String firstName;
    @Column(name = "LAST_NAME", nullable = false, length = 100) private String lastName;
    @Column(name = "EMAIL", nullable = false, unique = true, length = 320) private String email;
    @Column(name = "MOBILE_NUMBER", nullable = false, unique = true, length = 30) private String mobileNumber;
    @Column(name = "DATE_OF_BIRTH", nullable = false) private LocalDate dateOfBirth;
    @Column(name = "COUNTRY", length = 100) private String country;
    @Column(name = "CITY", length = 100) private String city;
    @Column(name = "TIMEZONE", length = 100) private String timezone;
    @Column(name = "LINKEDIN_URL", length = 500) private String linkedinUrl;
    @Column(name = "TOTAL_EXPERIENCE_YEARS", precision = 4, scale = 1) private BigDecimal totalExperienceYears;
    @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 30) private ExpertStatus status = ExpertStatus.SUBMITTED;
    @Column(name = "REVIEW_REASON", length = 1000) private String reviewReason;
    @Column(name = "REVIEWED_BY", length = 150) private String reviewedBy;
    @Column(name = "REVIEWED_AT") private LocalDateTime reviewedAt;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT", nullable = false) private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "expert", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("skillOrder ASC") private List<ExpertSkill> skills = new ArrayList<>();
    @OneToOne(mappedBy = "expert", cascade = CascadeType.ALL, orphanRemoval = true) private ExpertExpertise expertise;
    @OneToOne(mappedBy = "expert", cascade = CascadeType.ALL, orphanRemoval = true) private ExpertAvailability availability;
    @OneToOne(mappedBy = "expert", cascade = CascadeType.ALL, orphanRemoval = true) private ExpertConsulting consulting;
    @OneToOne(mappedBy = "expert", cascade = CascadeType.ALL, orphanRemoval = true) private ExpertVerification verification;

    @PrePersist void prePersist() { LocalDateTime now = LocalDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void preUpdate() { updatedAt = LocalDateTime.now(); }
    public void addSkill(ExpertSkill v) { skills.add(v); v.setExpert(this); }
    public void setExpertise(ExpertExpertise v) { expertise = v; if (v != null) v.setExpert(this); }
    public void setAvailability(ExpertAvailability v) { availability = v; if (v != null) v.setExpert(this); }
    public void setConsulting(ExpertConsulting v) { consulting = v; if (v != null) v.setExpert(this); }
    public void setVerification(ExpertVerification v) { verification = v; if (v != null) v.setExpert(this); }
    public Long getId() { return id; } public String getFirstName() { return firstName; } public void setFirstName(String v) { firstName = v; }
    public String getLastName() { return lastName; } public void setLastName(String v) { lastName = v; } public String getEmail() { return email; } public void setEmail(String v) { email = v; }
    public String getMobileNumber() { return mobileNumber; } public void setMobileNumber(String v) { mobileNumber = v; } public LocalDate getDateOfBirth() { return dateOfBirth; } public void setDateOfBirth(LocalDate v) { dateOfBirth = v; }
    public String getCountry() { return country; } public void setCountry(String v) { country = v; } public String getCity() { return city; } public void setCity(String v) { city = v; }
    public String getTimezone() { return timezone; } public void setTimezone(String v) { timezone = v; } public String getLinkedinUrl() { return linkedinUrl; } public void setLinkedinUrl(String v) { linkedinUrl = v; }
    public BigDecimal getTotalExperienceYears() { return totalExperienceYears; } public void setTotalExperienceYears(BigDecimal v) { totalExperienceYears = v; }
    public ExpertStatus getStatus() { return status; } public void setStatus(ExpertStatus v) { status = v; }
    public String getReviewReason() { return reviewReason; } public void setReviewReason(String v) { reviewReason = v; }
    public String getReviewedBy() { return reviewedBy; } public void setReviewedBy(String v) { reviewedBy = v; } public LocalDateTime getReviewedAt() { return reviewedAt; } public void setReviewedAt(LocalDateTime v) { reviewedAt = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public LocalDateTime getUpdatedAt() { return updatedAt; } public List<ExpertSkill> getSkills() { return skills; }
    public ExpertExpertise getExpertise() { return expertise; } public ExpertAvailability getAvailability() { return availability; } public ExpertConsulting getConsulting() { return consulting; } public ExpertVerification getVerification() { return verification; }
}
