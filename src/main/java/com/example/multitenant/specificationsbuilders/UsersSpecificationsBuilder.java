package com.example.multitenant.specificationsbuilders;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.dtos.membership.MembershipFilter;
import com.example.multitenant.dtos.users.UsersFilter;
import com.example.multitenant.models.User;
import com.example.multitenant.specifications.UsersSpecifications;

public class UsersSpecificationsBuilder {
    public static Specification<User> build(UsersFilter filter) {
        Specification<User> spec = Specification.where(null);
 
        if(filter.getEmail() != null && !filter.getEmail().isBlank()) {
            spec = spec.and(UsersSpecifications.hasEmail(filter.getEmail()));
        }

        if(filter.getFirstName() != null && !filter.getFirstName().isBlank()) {
            spec = spec.and(UsersSpecifications.hasFirstName(filter.getFirstName()));
        }

        if(filter.getLastName() != null && !filter.getLastName().isBlank()) {
            spec = spec.and(UsersSpecifications.hasLastName(filter.getLastName()));
        }

        return spec == null ? Specification.where(null) : spec;
    }
}
