package com.example.multitenant.specificationsbuilders;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.dtos.organizations.OrganizationsFilter;
import com.example.multitenant.dtos.users.UsersFilter;
import com.example.multitenant.models.Organization;
import com.example.multitenant.specifications.OrganizationsSpecifications;
import com.example.multitenant.specifications.UsersSpecifications;

public class OrganizationsSpecificationsBuilder {
    public static Specification<Organization> build(OrganizationsFilter filter) {
        Specification<Organization> spec = Specification.where(null);
 
        if(filter.getName() != null && !filter.getName().isBlank()) {
            spec = spec.and(OrganizationsSpecifications.hasName(filter.getName()));
        }

        return spec;
    }
}
