package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.Expert;
import com.neshtek.expertconnect.entity.ExpertStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class ExpertSpecifications {
    private ExpertSpecifications() { }

    public static Specification<Expert> hasSkill(String skill) {
        return (root, query, cb) -> {
            if (skill == null || skill.isBlank()) return null;
            query.distinct(true);
            var join = root.join("skills");
            return cb.equal(cb.lower(join.get("skillName")), skill.trim().toLowerCase());
        };
    }

    public static Specification<Expert> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) return null;
            try {
                return cb.equal(root.get("status"), ExpertStatus.valueOf(status.trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid expert status: " + status);
            }
        };
    }

    public static Specification<Expert> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isBlank()) return null;
            return cb.equal(cb.lower(root.get("city")), city.trim().toLowerCase());
        };
    }

    public static Specification<Expert> hasCountry(String country) {
        return (root, query, cb) -> {
            if (country == null || country.isBlank()) return null;
            return cb.equal(cb.lower(root.get("country")), country.trim().toLowerCase());
        };
    }

    public static Specification<Expert> hasMinimumExperience(BigDecimal minimumExperience) {
        return (root, query, cb) -> {
            if (minimumExperience == null) return null;
            return cb.greaterThanOrEqualTo(root.get("totalExperienceYears"), minimumExperience);
        };
    }

    public static Specification<Expert> hasAvailability(Boolean available) {
        return (root, query, cb) -> {
            if (available == null) return null;
            query.distinct(true);
            var join = root.join("availability", jakarta.persistence.criteria.JoinType.LEFT);
            if (!available) return cb.isNull(join.get("expertId"));
            return cb.and(
                    cb.isNotNull(join.get("expertId")),
                    cb.or(
                            cb.isNull(join.get("availableFrom")),
                            cb.lessThanOrEqualTo(join.get("availableFrom"), LocalDate.now())
                    )
            );
        };
    }

    public static Specification<Expert> hasMaximumHourlyRate(BigDecimal maxHourlyRate) {
        return (root, query, cb) -> {
            if (maxHourlyRate == null) return null;
            var join = root.join("consulting", jakarta.persistence.criteria.JoinType.LEFT);
            return cb.and(
                    cb.isNotNull(join.get("hourlyRate")),
                    cb.lessThanOrEqualTo(join.get("hourlyRate"), maxHourlyRate)
            );
        };
    }

    public static Specification<Expert> hasCurrency(String currency) {
        return (root, query, cb) -> {
            if (currency == null || currency.isBlank()) return null;
            var join = root.join("consulting", jakarta.persistence.criteria.JoinType.LEFT);
            return cb.equal(cb.upper(join.get("currencyCode")), currency.trim().toUpperCase());
        };
    }
}
