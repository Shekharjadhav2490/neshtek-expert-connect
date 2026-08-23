package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.PublicExpertSearchResponse;
import com.neshtek.expertconnect.entity.Expert;
import com.neshtek.expertconnect.entity.ExpertAvailability;
import com.neshtek.expertconnect.repository.ExpertRepository;
import com.neshtek.expertconnect.repository.ExpertSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PublicExpertSearchService {
    private final ExpertRepository repository;

    public PublicExpertSearchService(ExpertRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<PublicExpertSearchResponse> search(
            String skill,
            String city,
            String country,
            BigDecimal minExperience,
            Boolean available,
            BigDecimal maxHourlyRate,
            String currency,
            Pageable pageable) {

        Specification<Expert> specification = Specification.allOf(
                ExpertSpecifications.hasStatus("ACTIVE"),
                ExpertSpecifications.hasSkill(skill),
                ExpertSpecifications.hasCity(city),
                ExpertSpecifications.hasCountry(country),
                ExpertSpecifications.hasMinimumExperience(minExperience),
                ExpertSpecifications.hasAvailability(available),
                ExpertSpecifications.hasMaximumHourlyRate(maxHourlyRate),
                ExpertSpecifications.hasCurrency(currency));

        return repository.findAll(specification, pageable).map(this::toResponse);
    }

    private PublicExpertSearchResponse toResponse(Expert expert) {
        ExpertAvailability availability = expert.getAvailability();
        boolean available = availability != null
                && (availability.getAvailableFrom() == null
                || !availability.getAvailableFrom().isAfter(java.time.LocalDate.now()));

        return new PublicExpertSearchResponse(
                expert.getId(),
                expert.getFirstName(),
                expert.getLastName(),
                expert.getCity(),
                expert.getCountry(),
                expert.getTimezone(),
                expert.getTotalExperienceYears(),
                expert.getSkills().stream().map(s -> s.getSkillName()).toList(),
                available,
                availability == null ? null : availability.getHoursPerWeek(),
                expert.getConsulting() == null ? null : expert.getConsulting().getCurrencyCode(),
                expert.getConsulting() == null ? null : expert.getConsulting().getHourlyRate()
        );
    }
}
