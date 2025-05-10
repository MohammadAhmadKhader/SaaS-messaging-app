package com.example.multitenant.specifications;

import org.springframework.data.jpa.domain.Specification;
import com.example.multitenant.models.OrgRestriction;
import jakarta.persistence.criteria.*;

public class OrgsRestrictionsSpec {
    public static Specification<OrgRestriction> hasOrganizationId(Integer id) {
        return (Root<OrgRestriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("organization").get("id"), id);
        };
    }

    public static Specification<OrgRestriction> hasUserId(Long id) {
        return (Root<OrgRestriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("user").get("id"), id);
        };
    }

    public static Specification<OrgRestriction> hasCreatedBy(Long id) {
        return (Root<OrgRestriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("createdBy").get("id"), id);
        };
    }

    public static Specification<OrgRestriction> hasReason(String reason) {
        return (Root<OrgRestriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("reason"), reason);
        };
    }

    public static Specification<OrgRestriction> isActive(Boolean isActive) {
        return (Root<OrgRestriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            if (isActive) {
                return cb.greaterThan(root.get("until"), cb.currentTimestamp());
            }
            
            return cb.lessThanOrEqualTo(root.get("until"), cb.currentTimestamp());
        };
    }
}