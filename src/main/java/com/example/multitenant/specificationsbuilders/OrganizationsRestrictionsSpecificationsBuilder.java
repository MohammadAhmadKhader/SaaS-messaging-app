package com.example.multitenant.specificationsbuilders;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.dtos.organizationrestrictions.OrgRestrictionsFilter;
import com.example.multitenant.dtos.organizations.OrganizationsFilter;
import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.models.Organization;
import com.example.multitenant.models.OrganizationRestriction;
import com.example.multitenant.specifications.OrganizationsRestrictionsSepcifications;
import com.example.multitenant.specifications.OrganizationsSpecifications;

public class OrganizationsRestrictionsSpecificationsBuilder {
    public static Specification<OrganizationRestriction> build(OrgRestrictionsFilter filter) {
        if(filter.getOrganizationId() == null) {
            // 'IllegalStateException' was used because we want to hide this message from the response
            // user should not be able tor each here jsut in case he does, this will ensure this error is away from the user
            throw new IllegalStateException("organization id must be provided");
        }

        var orgIdSpec = OrganizationsRestrictionsSepcifications.hasOrganizationId(filter.getOrganizationId());
        var spec = Specification.where(orgIdSpec);
 
        if(filter.getIsActive() != null) {
            spec = spec.and(OrganizationsRestrictionsSepcifications.isActive(filter.getIsActive()));
        }

        if(filter.getReason() != null && !filter.getReason().isBlank()) {
            spec = spec.and(OrganizationsRestrictionsSepcifications.hasReason(filter.getReason()));
        }

        if(filter.getUserId() != null) {
            spec = spec.and(OrganizationsRestrictionsSepcifications.hasUserId(filter.getUserId()));
        }

        if(filter.getCreatedById() != null) {
            spec = spec.and(OrganizationsRestrictionsSepcifications.hasCreatedBy(filter.getCreatedById()));
        }

        return spec;
    }
}
