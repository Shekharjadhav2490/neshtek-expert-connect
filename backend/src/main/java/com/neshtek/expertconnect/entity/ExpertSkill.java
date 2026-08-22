package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "EXPERT_SKILL")
public class ExpertSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXPERT_SKILL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EXPERT_ID", nullable = false)
    private Expert expert;

    @Column(name = "SKILL_ORDER", nullable = false)
    private Integer skillOrder;

    @Column(name = "SKILL_NAME", nullable = false, length = 150)
    private String skillName;

    @Column(name = "SKILL_LEVEL", length = 30)
    private String skillLevel;

    @Column(name = "YEARS_EXPERIENCE", precision = 4, scale = 1)
    private BigDecimal yearsExperience;

    public Long getId() { return id; }
    public Expert getExpert() { return expert; }
    public void setExpert(Expert v) { expert = v; }
    public Integer getSkillOrder() { return skillOrder; }
    public void setSkillOrder(Integer v) { skillOrder = v; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String v) { skillName = v; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String v) { skillLevel = v; }
    public BigDecimal getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(BigDecimal v) { yearsExperience = v; }
}
