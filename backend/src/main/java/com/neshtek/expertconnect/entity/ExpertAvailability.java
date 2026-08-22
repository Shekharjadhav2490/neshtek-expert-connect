package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "EXPERT_AVAILABILITY")
public class ExpertAvailability {
    @Id
    @Column(name = "EXPERT_ID")
    private Long expertId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "EXPERT_ID")
    private Expert expert;

    @Column(name = "HOURS_PER_WEEK", precision = 5, scale = 1)
    private BigDecimal hoursPerWeek;

    @Column(name = "AVAILABLE_FROM")
    private LocalDate availableFrom;

    @Column(name = "TIMEZONE", length = 100)
    private String timezone;

    @Column(name = "WEEKDAY_AVAILABLE", length = 1)
    private String weekdayAvailable = "Y";

    @Column(name = "WEEKEND_AVAILABLE", length = 1)
    private String weekendAvailable = "N";

    public Long getExpertId() { return expertId; }
    public Expert getExpert() { return expert; }
    public void setExpert(Expert v) { expert = v; }
    public BigDecimal getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(BigDecimal v) { hoursPerWeek = v; }
    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate v) { availableFrom = v; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String v) { timezone = v; }
    public String getWeekdayAvailable() { return weekdayAvailable; }
    public void setWeekdayAvailable(String v) { weekdayAvailable = v; }
    public String getWeekendAvailable() { return weekendAvailable; }
    public void setWeekendAvailable(String v) { weekendAvailable = v; }
}
