package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.PublicExpertProfileResponse;
import com.neshtek.expertconnect.entity.Expert;
import com.neshtek.expertconnect.entity.ExpertAvailability;
import com.neshtek.expertconnect.entity.ExpertConsulting;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.ExpertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PublicExpertProfileService {
    private final ExpertRepository repository;

    public PublicExpertProfileService(ExpertRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PublicExpertProfileResponse getActiveProfile(Long id) {
        Expert expert = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expert not found: " + id));

        if (expert.getStatus() != com.neshtek.expertconnect.entity.ExpertStatus.ACTIVE) {
            throw new ResourceNotFoundException("Public expert profile not available: " + id);
        }

        ExpertAvailability availability = expert.getAvailability();
        ExpertConsulting consulting = expert.getConsulting();

        boolean available = availability != null
                && (availability.getAvailableFrom() == null
                || !availability.getAvailableFrom().isAfter(LocalDate.now()));

        return new PublicExpertProfileResponse(
                expert.getId(),
                expert.getFirstName(),
                expert.getLastName(),
                expert.getCity(),
                expert.getCountry(),
                expert.getTimezone(),
                expert.getTotalExperienceYears(),
                expert.getExpertise() == null ? null : expert.getExpertise().getTechnicalExpertise(),
                expert.getSkills().stream().map(s -> s.getSkillName()).toList(),
                expert.getStatus().name(),
                available,
                availability == null ? null : availability.getHoursPerWeek(),
                consulting == null ? null : consulting.getCurrencyCode(),
                consulting == null ? null : consulting.getHourlyRate()
        );
    }
}
