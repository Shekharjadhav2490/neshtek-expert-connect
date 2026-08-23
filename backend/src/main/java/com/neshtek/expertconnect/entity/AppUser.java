package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "APP_USER")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID") private Long id;
    @Column(name = "EMAIL", nullable = false, unique = true, length = 320) private String email;
    @Column(name = "PASSWORD_HASH", nullable = false, length = 255) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(name = "ROLE", nullable = false, length = 30) private AppUserRole role;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "CUSTOMER_ID") private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "EXPERT_ID") private Expert expert;
    @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 20) private AppUserStatus status = AppUserStatus.ACTIVE;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT", nullable = false) private LocalDateTime updatedAt;
    @PrePersist void prePersist(){var now=LocalDateTime.now();createdAt=now;updatedAt=now;}
    @PreUpdate void preUpdate(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
    public AppUserRole getRole(){return role;} public void setRole(AppUserRole v){role=v;}
    public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;}
    public Expert getExpert(){return expert;} public void setExpert(Expert v){expert=v;}
    public AppUserStatus getStatus(){return status;} public void setStatus(AppUserStatus v){status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
