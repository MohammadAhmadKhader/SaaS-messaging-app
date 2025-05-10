package com.example.multitenant.specifications;

import org.springframework.data.jpa.domain.Specification;
import com.example.multitenant.models.Restriction;
import jakarta.persistence.criteria.*;

public class RestrictionsSpec {
    public static Specification<Restriction> hasUserId(Long id) {
        return (Root<Restriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("user").get("id"), id);
        };
    }

    public static Specification<Restriction> hasCreatedBy(Long id) {
        return (Root<Restriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("createdBy").get("id"), id);
        };
    }

    public static Specification<Restriction> hasReason(String reason) {
        return (Root<Restriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("reason"), reason);
        };
    }

    public static Specification<Restriction> isActive(Boolean isActive) {
        return (Root<Restriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            if (isActive) {
                return cb.greaterThan(root.get("until"), cb.currentTimestamp());
            }
            
            return cb.lessThanOrEqualTo(root.get("until"), cb.currentTimestamp());
        };
    }
}
