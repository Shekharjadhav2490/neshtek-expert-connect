package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOMER")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMER_ID")
    private Long id;

    @Column(name = "COMPANY_NAME", nullable = false, length = 200) private String companyName;
    @Column(name = "CONTACT_NAME", nullable = false, length = 150) private String contactName;
    @Column(name = "EMAIL", nullable = false, unique = true, length = 320) private String email;
    @Column(name = "PHONE", length = 30) private String phone;
    @Column(name = "COUNTRY", length = 100) private String country;
    @Column(name = "CITY", length = 100) private String city;
    @Column(name = "TIMEZONE", length = 100) private String timezone;
    @Column(name = "INDUSTRY", length = 150) private String industry;
    @Column(name = "COMPANY_SIZE", length = 50) private String companySize;
    @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 20) private CustomerStatus status = CustomerStatus.ACTIVE;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT", nullable = false) private LocalDateTime updatedAt;

    @PrePersist void prePersist() { LocalDateTime now = LocalDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void preUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getCompanyName() { return companyName; } public void setCompanyName(String v) { companyName = v; }
    public String getContactName() { return contactName; } public void setContactName(String v) { contactName = v; }
    public String getEmail() { return email; } public void setEmail(String v) { email = v; }
    public String getPhone() { return phone; } public void setPhone(String v) { phone = v; }
    public String getCountry() { return country; } public void setCountry(String v) { country = v; }
    public String getCity() { return city; } public void setCity(String v) { city = v; }
    public String getTimezone() { return timezone; } public void setTimezone(String v) { timezone = v; }
    public String getIndustry() { return industry; } public void setIndustry(String v) { industry = v; }
    public String getCompanySize() { return companySize; } public void setCompanySize(String v) { companySize = v; }
    public CustomerStatus getStatus() { return status; } public void setStatus(CustomerStatus v) { status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public LocalDateTime getUpdatedAt() { return updatedAt; }
}
