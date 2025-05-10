package com.example.multitenant.specificationsbuilders;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.dtos.organizationrestrictions.OrgRestrictionsFilter;
import com.example.multitenant.dtos.organizations.OrgsFilter;
import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.models.Organization;
import com.example.multitenant.models.OrgRestriction;
import com.example.multitenant.specifications.OrgsRestrictionsSpec;
import com.example.multitenant.specifications.OrgsSpec;

public class OrgsRestrictionsSpecBuilder {
    public static Specification<OrgRestriction> build(OrgRestrictionsFilter filter) {
        if(filter.getOrganizationId() == null) {
            // 'IllegalStateException' was used because we want to hide this message from the response
            // user should not be able tor each here jsut in case he does, this will ensure this error is away from the user
            throw new IllegalStateException("organization id must be provided");
        }

        var orgIdSpec = OrgsRestrictionsSpec.hasOrganizationId(filter.getOrganizationId());
        var spec = Specification.where(orgIdSpec);
 
        if(filter.getIsActive() != null) {
            spec = spec.and(OrgsRestrictionsSpec.isActive(filter.getIsActive()));
        }

        if(filter.getReason() != null && !filter.getReason().isBlank()) {
            spec = spec.and(OrgsRestrictionsSpec.hasReason(filter.getReason()));
        }

        if(filter.getUserId() != null) {
            spec = spec.and(OrgsRestrictionsSpec.hasUserId(filter.getUserId()));
        }

        if(filter.getCreatedById() != null) {
            spec = spec.and(OrgsRestrictionsSpec.hasCreatedBy(filter.getCreatedById()));
        }

        return spec;
    }
}
