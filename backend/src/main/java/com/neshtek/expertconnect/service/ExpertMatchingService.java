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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpertMatchingService {
    private static final double MANDATORY_SKILL_WEIGHT = 40.0;
    private static final double OPTIONAL_SKILL_WEIGHT = 10.0;
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

        List<CustomerRequirementSkill> requiredSkillEntities = requirement.getSkills().stream()
                .filter(s -> s.getSkillName() != null && !s.getSkillName().isBlank())
                .toList();

        List<String> requiredSkills = requiredSkillEntities.stream()
                .map(CustomerRequirementSkill::getSkillName)
                .map(this::canonicalSkillKey)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        if (requiredSkills.isEmpty() && isBlank(requirement.getTechnology())) return List.of();

        int safeLimit = Math.min(Math.max(limit, 1), 20);
        return expertRepository.findAll().stream()
                .filter(e -> e.getStatus() == ExpertStatus.ACTIVE)
                .map(e -> score(requirement, requiredSkillEntities, requiredSkills, e))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ExpertMatchResponse::matchScore).reversed()
                        .thenComparing(ExpertMatchResponse::expertId))
                .limit(safeLimit)
                .toList();
    }

    private ExpertMatchResponse score(CustomerRequirement requirement,
                                       List<CustomerRequirementSkill> requiredSkillEntities,
                                       List<String> requiredSkills, Expert expert) {
        Set<String> expertSkills = expert.getSkills().stream()
                .map(ExpertSkill::getSkillName)
                .filter(Objects::nonNull)
                .map(this::canonicalSkillKey)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        List<String> matchedNames = new ArrayList<>();
        List<String> missingMandatory = new ArrayList<>();
        List<String> missingOptional = new ArrayList<>();
        int mandatoryRequired = 0, mandatoryMatched = 0, optionalRequired = 0, optionalMatched = 0;

        for (CustomerRequirementSkill requiredSkill : requiredSkillEntities) {
            String required = canonicalSkillKey(requiredSkill.getSkillName());
            if (required.isBlank()) continue;
            boolean matched = expertSkills.contains(required);
            if (requiredSkill.isMandatory()) {
                mandatoryRequired++;
                if (matched) mandatoryMatched++; else missingMandatory.add(requiredSkill.getSkillName());
            } else {
                optionalRequired++;
                if (matched) optionalMatched++; else missingOptional.add(requiredSkill.getSkillName());
            }
            if (matched) matchedNames.add(requiredSkill.getSkillName());
        }

        double mandatoryRatio = mandatoryRequired == 0 ? 1.0 : (double) mandatoryMatched / mandatoryRequired;
        double optionalRatio = optionalRequired == 0 ? 1.0 : (double) optionalMatched / optionalRequired;
        double skillScore = mandatoryRatio * MANDATORY_SKILL_WEIGHT + optionalRatio * OPTIONAL_SKILL_WEIGHT;

        boolean experienceMatch = requirement.getRequiredExperienceYears() == null
                || (expert.getTotalExperienceYears() != null
                && expert.getTotalExperienceYears().compareTo(requirement.getRequiredExperienceYears()) >= 0);
        double experienceRatio = requirement.getRequiredExperienceYears() == null || requirement.getRequiredExperienceYears().signum() == 0
                ? 1.0 : experienceMatch ? 1.0 : Math.min(1.0,
                expert.getTotalExperienceYears() == null ? 0.0 : expert.getTotalExperienceYears().doubleValue()
                        / requirement.getRequiredExperienceYears().doubleValue());

        boolean availabilityMatch = availabilityMatches(requirement, expert);
        boolean technologyMatch = technologyMatches(requirement.getTechnology(), expert, expertSkills);
        double score = skillScore + experienceRatio * EXPERIENCE_WEIGHT
                + (availabilityMatch ? AVAILABILITY_WEIGHT : 0.0)
                + (technologyMatch ? TECHNOLOGY_WEIGHT : 0.0);
        if (score <= 0.0) return null;

        BigDecimal hourlyRate = expert.getConsulting() == null ? null : expert.getConsulting().getHourlyRate();
        String currency = expert.getConsulting() == null ? null : expert.getConsulting().getCurrencyCode();
        BigDecimal roundedScore = BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
        boolean mandatorySatisfied = missingMandatory.isEmpty();
        String level = matchLevel(roundedScore.doubleValue(), mandatorySatisfied);
        String recommendation = recommendation(mandatorySatisfied, missingMandatory, missingOptional,
                experienceMatch, availabilityMatch, technologyMatch);

        return new ExpertMatchResponse(expert.getId(), expert.getFirstName(), expert.getLastName(), expert.getCity(),
                expert.getTimezone(), expert.getTotalExperienceYears(), hourlyRate, currency, roundedScore,
                matchedNames.size(), requiredSkills.size(), matchedNames, mandatoryMatched, mandatoryRequired,
                optionalMatched, optionalRequired, missingMandatory, missingOptional, mandatorySatisfied,
                experienceMatch, availabilityMatch, technologyMatch, level, recommendation);
    }

    private String matchLevel(double score, boolean mandatorySatisfied) {
        if (!mandatorySatisfied) return score >= 60 ? "PARTIAL_MATCH" : "LOW_MATCH";
        if (score >= 90) return "EXCELLENT_MATCH";
        if (score >= 75) return "GOOD_MATCH";
        if (score >= 60) return "FAIR_MATCH";
        return "LOW_MATCH";
    }

    private String recommendation(boolean mandatorySatisfied, List<String> missingMandatory,
                                  List<String> missingOptional, boolean experienceMatch,
                                  boolean availabilityMatch, boolean technologyMatch) {
        if (!mandatorySatisfied) return "Not all mandatory skills are matched. Missing: "
                + String.join(", ", missingMandatory) + ".";
        List<String> strengths = new ArrayList<>();
        if (experienceMatch) strengths.add("experience");
        if (availabilityMatch) strengths.add("availability");
        if (technologyMatch) strengths.add("technology");
        if (!missingOptional.isEmpty()) return "Mandatory skills are satisfied; strong "
                + String.join(", ", strengths) + ". Optional skills not matched: "
                + String.join(", ", missingOptional) + ".";
        return "All mandatory and optional skills are matched with " + String.join(", ", strengths) + ".";
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
        String tech = canonicalSkillKey(technology);
        if (expertSkills.contains(tech)) return true;
        String expertise = expert.getExpertise() == null ? "" : normalize(expert.getExpertise().getTechnicalExpertise());
        return !expertise.isBlank() && (expertise.contains(normalize(technology)) || expertise.contains(tech));
    }

    /** Curated enterprise aliases; intentionally avoids broad fuzzy matching. */
    private String canonicalSkillKey(String value) {
        String normalized = normalize(value);
        if (normalized.isBlank()) return "";
        if (Set.of("oracle webcenter content", "oracle wcc", "wcc", "webcenter content",
                "oracle ucm", "ucm", "oracle universal content management", "webcenter").contains(normalized))
            return "oracle webcenter content";
        if (Set.of("oracle database", "oracle db", "oracle rdbms", "oracle sql", "oracle sql database").contains(normalized))
            return "oracle database";
        if (Set.of("oracle cloud", "oracle cloud infrastructure", "oci").contains(normalized)) return "oracle cloud";
        if (Set.of("oracle weblogic", "oracle weblogic server", "weblogic", "wls").contains(normalized)) return "oracle weblogic";
        if (Set.of("spring boot", "springboot", "spring-boot").contains(normalized)) return "spring boot";
        if (Set.of("javascript", "js").contains(normalized)) return "javascript";
        if (Set.of("typescript", "ts").contains(normalized)) return "typescript";
        if (Set.of("angular", "angular framework").contains(normalized)) return "angular";
        if (Set.of("postgres", "postgresql", "postgres sql").contains(normalized)) return "postgresql";
        return normalized;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
}
