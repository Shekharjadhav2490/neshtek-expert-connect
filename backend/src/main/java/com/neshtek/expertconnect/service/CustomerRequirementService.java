package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.CustomerRequirementRequest;
import com.neshtek.expertconnect.dto.CustomerRequirementResponse;
import com.neshtek.expertconnect.entity.CustomerRequirement;
import com.neshtek.expertconnect.entity.CustomerRequirementSkill;
import com.neshtek.expertconnect.entity.CustomerRequirementStatus;
import com.neshtek.expertconnect.entity.RequirementPriority;
import com.neshtek.expertconnect.repository.CustomerRequirementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Locale;

@Service
public class CustomerRequirementService {
    private final CustomerRequirementRepository repository;
    public CustomerRequirementService(CustomerRequirementRepository repository){this.repository=repository;}

    @Transactional
    public CustomerRequirementResponse create(CustomerRequirementRequest request){
        CustomerRequirement entity = new CustomerRequirement();
        entity.setCompanyName(request.companyName().trim()); entity.setContactName(request.contactName().trim());
        entity.setEmail(request.email().trim().toLowerCase(Locale.ROOT)); entity.setPhone(request.phone()); entity.setCountry(request.country()); entity.setCity(request.city());
        entity.setTitle(request.title().trim()); entity.setDescription(request.description().trim()); entity.setTechnology(request.technology());
        entity.setRequiredExperienceYears(request.requiredExperienceYears()); entity.setEstimatedHours(request.estimatedHours()); entity.setPreferredStartDate(request.preferredStartDate());
        entity.setPriority(parsePriority(request.priority())); entity.setBudget(request.budget()); entity.setCurrencyCode(request.currencyCode()==null?"USD":request.currencyCode().toUpperCase(Locale.ROOT));
        entity.setStatus(CustomerRequirementStatus.SUBMITTED);
        if(request.skills()!=null){
            int order=1;
            for(CustomerRequirementRequest.SkillRequest s:request.skills()){
                CustomerRequirementSkill skill=new CustomerRequirementSkill(); skill.setSkillName(s.skillName().trim()); skill.setPriorityOrder(s.priorityOrder()==null?order:s.priorityOrder()); entity.addSkill(skill); order++;
            }
        }
        return toResponse(repository.save(entity));
    }

    @Transactional
    public CustomerRequirementResponse get(Long id){return toResponse(repository.findById(id).orElseThrow(()->new EntityNotFoundException("Customer requirement not found: "+id)));}
    @Transactional
    public Page<CustomerRequirementResponse> list(Pageable pageable){return repository.findAll(pageable).map(this::toResponse);}

    private RequirementPriority parsePriority(String value){
        if(value==null || value.isBlank()) return RequirementPriority.MEDIUM;
        try{return RequirementPriority.valueOf(value.trim().toUpperCase(Locale.ROOT));}
        catch(IllegalArgumentException ex){throw new IllegalArgumentException("Priority must be LOW, MEDIUM, HIGH or URGENT");}
    }
    private CustomerRequirementResponse toResponse(CustomerRequirement e){
        var skills=new ArrayList<CustomerRequirementResponse.SkillResponse>();
        for(CustomerRequirementSkill s:e.getSkills()) skills.add(new CustomerRequirementResponse.SkillResponse(s.getId(),s.getSkillName(),s.getPriorityOrder()));
        return new CustomerRequirementResponse(e.getId(),e.getCompanyName(),e.getContactName(),e.getEmail(),e.getPhone(),e.getCountry(),e.getCity(),e.getTitle(),e.getDescription(),e.getTechnology(),e.getRequiredExperienceYears(),e.getEstimatedHours(),e.getPreferredStartDate(),e.getPriority().name(),e.getBudget(),e.getCurrencyCode(),e.getStatus().name(),e.getCreatedAt(),e.getUpdatedAt(),skills);
    }
}
