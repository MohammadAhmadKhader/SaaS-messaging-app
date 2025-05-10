package com.example.multitenant.specificationsbuilders;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.dtos.organizationrestrictions.OrgRestrictionsFilter;
import com.example.multitenant.dtos.restrictions.RestrictionsFilter;
import com.example.multitenant.models.OrganizationRestriction;
import com.example.multitenant.models.Restriction;
import com.example.multitenant.specifications.OrganizationsRestrictionsSepcifications;
import com.example.multitenant.specifications.RestrictionsSpecifications;

public class RestrictionsSpecificationsBuilder {
     public static Specification<Restriction> build(RestrictionsFilter filter) {
        Specification<Restriction> spec = Specification.where(null);
        if(filter.getIsActive() != null) {
            spec = spec.and(RestrictionsSpecifications.isActive(filter.getIsActive()));
        }

        if(filter.getReason() != null && !filter.getReason().isBlank()) {
            spec = spec.and(RestrictionsSpecifications.hasReason(filter.getReason()));
        }

        if(filter.getUserId() != null) {
            spec = spec.and(RestrictionsSpecifications.hasUserId(filter.getUserId()));
        }

        if(filter.getCreatedById() != null) {
            spec = spec.and(RestrictionsSpecifications.hasCreatedBy(filter.getCreatedById()));
        }

        return spec;
    }
}
