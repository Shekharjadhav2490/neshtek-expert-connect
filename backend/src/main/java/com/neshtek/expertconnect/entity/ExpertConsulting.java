package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "EXPERT_CONSULTING")
public class ExpertConsulting {
    @Id
    @Column(name = "EXPERT_ID")
    private Long expertId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "EXPERT_ID")
    private Expert expert;

    @Column(name = "MINIMUM_ENGAGEMENT_HOURS", precision = 5, scale = 1)
    private BigDecimal minimumEngagementHours;

    @Column(name = "MAXIMUM_ENGAGEMENT_HOURS", precision = 5, scale = 1)
    private BigDecimal maximumEngagementHours;

    @Column(name = "HOURLY_RATE", precision = 12, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "CURRENCY_CODE", length = 10)
    private String currencyCode;

    public Long getExpertId() { return expertId; }
    public Expert getExpert() { return expert; }
    public void setExpert(Expert v) { expert = v; }
    public BigDecimal getMinimumEngagementHours() { return minimumEngagementHours; }
    public void setMinimumEngagementHours(BigDecimal v) { minimumEngagementHours = v; }
    public BigDecimal getMaximumEngagementHours() { return maximumEngagementHours; }
    public void setMaximumEngagementHours(BigDecimal v) { maximumEngagementHours = v; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal v) { hourlyRate = v; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String v) { currencyCode = v; }
}
