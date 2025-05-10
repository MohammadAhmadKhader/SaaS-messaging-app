package com.example.multitenant.specificationsbuilders;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.dtos.organizations.OrgsFilter;
import com.example.multitenant.dtos.users.UsersFilter;
import com.example.multitenant.models.Organization;
import com.example.multitenant.specifications.OrgsSpec;
import com.example.multitenant.specifications.UsersSpec;

public class OrgsSpecBuilder {
    public static Specification<Organization> build(OrgsFilter filter) {
        Specification<Organization> spec = Specification.where(null);
 
        if(filter.getName() != null && !filter.getName().isBlank()) {
            spec = spec.and(OrgsSpec.hasName(filter.getName()));
        }

        return spec;
    }
}
