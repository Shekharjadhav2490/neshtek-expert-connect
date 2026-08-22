package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CUSTOMER_REQUIREMENT")
public class CustomerRequirement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUIREMENT_ID") private Long id;
    @Column(name = "COMPANY_NAME", nullable = false, length = 200) private String companyName;
    @Column(name = "CONTACT_NAME", nullable = false, length = 150) private String contactName;
    @Column(name = "EMAIL", nullable = false, length = 320) private String email;
    @Column(name = "PHONE", length = 30) private String phone;
    @Column(name = "COUNTRY", length = 100) private String country;
    @Column(name = "CITY", length = 100) private String city;
    @Column(name = "TITLE", nullable = false, length = 250) private String title;
    @Lob @Column(name = "DESCRIPTION", nullable = false) private String description;
    @Column(name = "TECHNOLOGY", length = 150) private String technology;
    @Column(name = "REQUIRED_EXPERIENCE_YEARS", precision = 4, scale = 1) private BigDecimal requiredExperienceYears;
    @Column(name = "ESTIMATED_HOURS", precision = 6, scale = 1) private BigDecimal estimatedHours;
    @Column(name = "PREFERRED_START_DATE") private LocalDate preferredStartDate;
    @Enumerated(EnumType.STRING) @Column(name = "PRIORITY", nullable = false, length = 20) private RequirementPriority priority = RequirementPriority.MEDIUM;
    @Column(name = "BUDGET", precision = 12, scale = 2) private BigDecimal budget;
    @Column(name = "CURRENCY_CODE", nullable = false, columnDefinition = "CHAR(3)") private String currencyCode = "USD";
    @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 30) private CustomerRequirementStatus status = CustomerRequirementStatus.SUBMITTED;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT", nullable = false) private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "requirement", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("priorityOrder ASC") private List<CustomerRequirementSkill> skills = new ArrayList<>();
    @PrePersist void prePersist(){LocalDateTime now=LocalDateTime.now();createdAt=now;updatedAt=now;}
    @PreUpdate void preUpdate(){updatedAt=LocalDateTime.now();}
    public void addSkill(CustomerRequirementSkill skill){skills.add(skill);skill.setRequirement(this);}
    public Long getId(){return id;} public String getCompanyName(){return companyName;} public void setCompanyName(String v){companyName=v;}
    public String getContactName(){return contactName;} public void setContactName(String v){contactName=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getCountry(){return country;} public void setCountry(String v){country=v;}
    public String getCity(){return city;} public void setCity(String v){city=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;} public String getTechnology(){return technology;} public void setTechnology(String v){technology=v;}
    public BigDecimal getRequiredExperienceYears(){return requiredExperienceYears;} public void setRequiredExperienceYears(BigDecimal v){requiredExperienceYears=v;} public BigDecimal getEstimatedHours(){return estimatedHours;} public void setEstimatedHours(BigDecimal v){estimatedHours=v;}
    public LocalDate getPreferredStartDate(){return preferredStartDate;} public void setPreferredStartDate(LocalDate v){preferredStartDate=v;} public RequirementPriority getPriority(){return priority;} public void setPriority(RequirementPriority v){priority=v;}
    public BigDecimal getBudget(){return budget;} public void setBudget(BigDecimal v){budget=v;} public String getCurrencyCode(){return currencyCode;} public void setCurrencyCode(String v){currencyCode=v;}
    public CustomerRequirementStatus getStatus(){return status;} public void setStatus(CustomerRequirementStatus v){status=v;} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;} public List<CustomerRequirementSkill> getSkills(){return skills;}
}
