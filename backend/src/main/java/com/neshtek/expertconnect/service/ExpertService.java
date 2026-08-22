package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.*;
import com.neshtek.expertconnect.entity.*;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.ExpertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class ExpertService {
    private static final int MIN_EXPERTISE_WORDS = 1000;
    private final ExpertRepository repository;

    public ExpertService(ExpertRepository repository) { this.repository = repository; }

    @Transactional
    public ExpertResponse register(ExpertRegistrationRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (repository.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("An expert with this email already exists");
        if (repository.existsByMobileNumber(request.mobileNumber().trim())) throw new IllegalArgumentException("An expert with this mobile number already exists");

        int wordCount = countWords(request.technicalExpertise());
        if (wordCount < MIN_EXPERTISE_WORDS) {
            throw new IllegalArgumentException("Technical expertise must contain at least 1000 words. Current count: " + wordCount);
        }

        Expert expert = new Expert();
        expert.setFirstName(request.firstName().trim());
        expert.setLastName(request.lastName().trim());
        expert.setEmail(email);
        expert.setMobileNumber(request.mobileNumber().trim());
        expert.setDateOfBirth(request.dateOfBirth());
        expert.setCountry(trim(request.country()));
        expert.setCity(trim(request.city()));
        expert.setTimezone(trim(request.timezone()));
        expert.setLinkedinUrl(trim(request.linkedinUrl()));
        expert.setTotalExperienceYears(request.totalExperienceYears());

        int order = 1;
        for (SkillRequest skill : request.skills()) {
            ExpertSkill entity = new ExpertSkill();
            entity.setSkillOrder(order++);
            entity.setSkillName(skill.skillName().trim());
            entity.setSkillLevel(trim(skill.skillLevel()));
            entity.setYearsExperience(skill.yearsExperience());
            expert.addSkill(entity);
        }

        ExpertExpertise expertise = new ExpertExpertise();
        expertise.setTechnicalExpertise(request.technicalExpertise().trim());
        expertise.setWordCount(wordCount);
        expert.setExpertise(expertise);

        if (request.availability() != null) {
            AvailabilityRequest a = request.availability();
            ExpertAvailability availability = new ExpertAvailability();
            availability.setHoursPerWeek(a.hoursPerWeek());
            availability.setAvailableFrom(a.availableFrom());
            availability.setTimezone(trim(a.timezone()));
            if (a.weekdayAvailable() != null) availability.setWeekdayAvailable(a.weekdayAvailable());
            if (a.weekendAvailable() != null) availability.setWeekendAvailable(a.weekendAvailable());
            expert.setAvailability(availability);
        }

        if (request.consulting() != null) {
            ConsultingRequest c = request.consulting();
            if (c.minimumEngagementHours() != null && c.maximumEngagementHours() != null && c.minimumEngagementHours().compareTo(c.maximumEngagementHours()) > 0) {
                throw new IllegalArgumentException("Minimum engagement hours cannot exceed maximum engagement hours");
            }
            ExpertConsulting consulting = new ExpertConsulting();
            consulting.setMinimumEngagementHours(c.minimumEngagementHours());
            consulting.setMaximumEngagementHours(c.maximumEngagementHours());
            consulting.setHourlyRate(c.hourlyRate());
            consulting.setCurrencyCode(trim(c.currencyCode()));
            expert.setConsulting(consulting);
        }

        return toResponse(repository.save(expert));
    }

    @Transactional(readOnly = true)
    public ExpertResponse get(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expert not found: " + id)));
    }

    private ExpertResponse toResponse(Expert expert) {
        return new ExpertResponse(expert.getId(), expert.getFirstName(), expert.getLastName(), expert.getEmail(),
                expert.getMobileNumber(), expert.getStatus(), expert.getSkills().size(),
                expert.getExpertise() == null ? 0 : expert.getExpertise().getWordCount(), expert.getCreatedAt());
    }

    private int countWords(String text) {
        String normalized = text == null ? "" : text.trim();
        return normalized.isEmpty() ? 0 : normalized.split("\\s+").length;
    }

    private String trim(String value) { return value == null ? null : value.trim(); }
}
