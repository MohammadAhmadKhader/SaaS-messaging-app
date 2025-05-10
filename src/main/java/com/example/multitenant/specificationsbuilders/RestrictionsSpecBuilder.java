package com.example.multitenant.specificationsbuilders;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.dtos.organizationrestrictions.OrgRestrictionsFilter;
import com.example.multitenant.dtos.restrictions.RestrictionsFilter;
import com.example.multitenant.models.OrgRestriction;
import com.example.multitenant.models.Restriction;
import com.example.multitenant.specifications.OrgsRestrictionsSpec;
import com.example.multitenant.specifications.RestrictionsSpec;

public class RestrictionsSpecBuilder {
     public static Specification<Restriction> build(RestrictionsFilter filter) {
        Specification<Restriction> spec = Specification.where(null);
        if(filter.getIsActive() != null) {
            spec = spec.and(RestrictionsSpec.isActive(filter.getIsActive()));
        }

        if(filter.getReason() != null && !filter.getReason().isBlank()) {
            spec = spec.and(RestrictionsSpec.hasReason(filter.getReason()));
        }

        if(filter.getUserId() != null) {
            spec = spec.and(RestrictionsSpec.hasUserId(filter.getUserId()));
        }

        if(filter.getCreatedById() != null) {
            spec = spec.and(RestrictionsSpec.hasCreatedBy(filter.getCreatedById()));
        }

        return spec;
    }
}
