package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.ExpertMatchResponse;
import com.neshtek.expertconnect.entity.CustomerRequirement;
import com.neshtek.expertconnect.entity.CustomerRequirementSkill;
import com.neshtek.expertconnect.entity.Expert;
import com.neshtek.expertconnect.entity.ExpertSkill;
import com.neshtek.expertconnect.entity.ExpertStatus;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.CustomerRequirementRepository;
import com.neshtek.expertconnect.repository.ExpertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpertMatchingService {
    private static final double SKILL_WEIGHT = 50.0;
    private static final double EXPERIENCE_WEIGHT = 20.0;
    private static final double AVAILABILITY_WEIGHT = 15.0;
    private static final double TECHNOLOGY_WEIGHT = 15.0;

    private final CustomerRequirementRepository requirementRepository;
    private final ExpertRepository expertRepository;

    public ExpertMatchingService(CustomerRequirementRepository requirementRepository,
                                  ExpertRepository expertRepository) {
        this.requirementRepository = requirementRepository;
        this.expertRepository = expertRepository;
    }

    @Transactional(readOnly = true)
    public List<ExpertMatchResponse> findMatches(Long requirementId, int limit) {
        CustomerRequirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer requirement not found: " + requirementId));

        List<String> requiredSkills = requirement.getSkills().stream()
                .map(CustomerRequirementSkill::getSkillName)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        if (requiredSkills.isEmpty() && isBlank(requirement.getTechnology())) {
            return List.of();
        }

        int safeLimit = Math.min(Math.max(limit, 1), 20);
        return expertRepository.findAll().stream()
                .filter(e -> e.getStatus() == ExpertStatus.ACTIVE)
                .map(e -> score(requirement, requiredSkills, e))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ExpertMatchResponse::matchScore).reversed()
                        .thenComparing(ExpertMatchResponse::expertId))
                .limit(safeLimit)
                .toList();
    }

    private ExpertMatchResponse score(CustomerRequirement requirement, List<String> requiredSkills, Expert expert) {
        Set<String> expertSkills = expert.getSkills().stream()
                .map(ExpertSkill::getSkillName)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .collect(Collectors.toSet());

        List<String> matchedNames = new ArrayList<>();
        for (String required : requiredSkills) {
            for (ExpertSkill skill : expert.getSkills()) {
                if (skill.getSkillName() != null && normalize(skill.getSkillName()).equals(required)) {
                    matchedNames.add(skill.getSkillName());
                    break;
                }
            }
        }

        double skillRatio = requiredSkills.isEmpty() ? 0.0 : (double) matchedNames.size() / requiredSkills.size();
        boolean experienceMatch = requirement.getRequiredExperienceYears() == null
                || (expert.getTotalExperienceYears() != null
                && expert.getTotalExperienceYears().compareTo(requirement.getRequiredExperienceYears()) >= 0);
        double experienceRatio = requirement.getRequiredExperienceYears() == null || requirement.getRequiredExperienceYears().signum() == 0
                ? 1.0 : experienceMatch ? 1.0 : Math.min(1.0,
                expert.getTotalExperienceYears() == null ? 0.0 : expert.getTotalExperienceYears().doubleValue()
                        / requirement.getRequiredExperienceYears().doubleValue());

        boolean availabilityMatch = availabilityMatches(requirement, expert);
        boolean technologyMatch = technologyMatches(requirement.getTechnology(), expert, expertSkills);

        double score = (skillRatio * SKILL_WEIGHT)
                + (experienceRatio * EXPERIENCE_WEIGHT)
                + (availabilityMatch ? AVAILABILITY_WEIGHT : 0.0)
                + (technologyMatch ? TECHNOLOGY_WEIGHT : 0.0);

        if (score <= 0.0) return null;

        BigDecimal hourlyRate = expert.getConsulting() == null ? null : expert.getConsulting().getHourlyRate();
        String currency = expert.getConsulting() == null ? null : expert.getConsulting().getCurrencyCode();

        return new ExpertMatchResponse(
                expert.getId(), expert.getFirstName(), expert.getLastName(), expert.getCity(), expert.getTimezone(),
                expert.getTotalExperienceYears(), hourlyRate, currency,
                BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP),
                matchedNames.size(), requiredSkills.size(), matchedNames,
                experienceMatch, availabilityMatch, technologyMatch);
    }

    private boolean availabilityMatches(CustomerRequirement requirement, Expert expert) {
        if (expert.getAvailability() == null) return false;
        if (expert.getAvailability().getAvailableFrom() != null && requirement.getPreferredStartDate() != null
                && expert.getAvailability().getAvailableFrom().isAfter(requirement.getPreferredStartDate())) return false;
        if (requirement.getEstimatedHours() != null && expert.getAvailability().getHoursPerWeek() != null
                && expert.getAvailability().getHoursPerWeek().compareTo(requirement.getEstimatedHours()) < 0) return false;
        return true;
    }

    private boolean technologyMatches(String technology, Expert expert, Set<String> expertSkills) {
        if (isBlank(technology)) return false;
        String tech = normalize(technology);
        if (expertSkills.stream().anyMatch(skill -> skill.equals(tech) || skill.contains(tech) || tech.contains(skill))) return true;
        String expertise = expert.getExpertise() == null ? "" : normalize(expert.getExpertise().getTechnicalExpertise());
        return !expertise.isBlank() && expertise.contains(tech);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
}
