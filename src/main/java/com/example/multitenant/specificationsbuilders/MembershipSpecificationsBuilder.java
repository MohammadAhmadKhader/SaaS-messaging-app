package com.example.multitenant.specificationsbuilders;

import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.dtos.membership.MembershipFilter;
import com.example.multitenant.models.Membership;
import com.example.multitenant.specifications.MembershipSpecifications;

// you have to specify for isMember in the function arguments directly that you want to allow "isMember field or not" for security purposes.
// you can allow isMember in filter by setting "isMember" in arguments as null
public class MembershipSpecificationsBuilder {
    public static Specification<Membership> build(MembershipFilter filter, Integer tenantId, Boolean isMember) {
        Specification<Membership> spec = Specification.where(null);
 
        if(filter.getEmail() != null) {
            spec = spec.and(MembershipSpecifications.hasEmail(filter.getEmail()));
        }

        if(filter.getFirstName() != null) {
            spec = spec.and(MembershipSpecifications.hasFirstName(filter.getFirstName()));
        }

        if(filter.getLastName() != null) {
            spec = spec.and(MembershipSpecifications.hasFirstName(filter.getLastName()));
        }

        if(filter.getJoinedAfter() != null) {
            spec = spec.and(MembershipSpecifications.hasJoinedAfter(filter.getJoinedAfter()));
        }

        if(filter.getJoinedBefore() != null) {
            spec = spec.and(MembershipSpecifications.hasJoinedAfter(filter.getJoinedBefore()));
        }

        if(tenantId != null) {
            spec = spec.and(MembershipSpecifications.hasOrganizationId(tenantId));
        }

        if(isMember != null) {
            spec = spec.and(MembershipSpecifications.isMember(isMember));
        }

        // you have to specify for isMember in the function arguments directly that you want to allow "isMember field or not" for security purposes
        if(isMember == null && filter.getIsMember() != null) {
            spec = spec.and(MembershipSpecifications.isMember(filter.getIsMember()));
        }

        return spec;
    }
}