package com.example.multitenant.specifications;

import org.springframework.data.jpa.domain.Specification;
import com.example.multitenant.models.OrganizationRestriction;
import jakarta.persistence.criteria.*;

public class OrganizationsRestrictionsSepcifications {
    public static Specification<OrganizationRestriction> hasOrganizationId(Integer id) {
        return (Root<OrganizationRestriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("organization").get("id"), id);
        };
    }

    public static Specification<OrganizationRestriction> hasUserId(Long id) {
        return (Root<OrganizationRestriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("user").get("id"), id);
        };
    }

    public static Specification<OrganizationRestriction> hasCreatedBy(Long id) {
        return (Root<OrganizationRestriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("createdBy").get("id"), id);
        };
    }

    public static Specification<OrganizationRestriction> hasReason(String reason) {
        return (Root<OrganizationRestriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("reason"), reason);
        };
    }

    public static Specification<OrganizationRestriction> isActive(Boolean isActive) {
        return (Root<OrganizationRestriction> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            if (isActive) {
                return cb.greaterThan(root.get("until"), cb.currentTimestamp());
            }
            
            return cb.lessThanOrEqualTo(root.get("until"), cb.currentTimestamp());
        };
    }
}