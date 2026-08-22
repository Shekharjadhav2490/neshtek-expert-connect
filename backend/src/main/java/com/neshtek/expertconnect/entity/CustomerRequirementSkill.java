package com.neshtek.expertconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CUSTOMER_REQUIREMENT_SKILL")
public class CustomerRequirementSkill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUIREMENT_SKILL_ID") private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "REQUIREMENT_ID", nullable = false) private CustomerRequirement requirement;
    @Column(name = "SKILL_NAME", nullable = false, length = 150) private String skillName;
    @Column(name = "PRIORITY_ORDER", nullable = false) private Integer priorityOrder = 1;
    public Long getId(){return id;} public CustomerRequirement getRequirement(){return requirement;} public void setRequirement(CustomerRequirement v){requirement=v;}
    public String getSkillName(){return skillName;} public void setSkillName(String v){skillName=v;} public Integer getPriorityOrder(){return priorityOrder;} public void setPriorityOrder(Integer v){priorityOrder=v;}
}
