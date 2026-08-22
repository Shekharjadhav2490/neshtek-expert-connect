package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.Expert;
import com.neshtek.expertconnect.entity.ExpertSkill;
import com.neshtek.expertconnect.entity.ExpertStatus;
import org.springframework.data.jpa.domain.Specification;

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
}
