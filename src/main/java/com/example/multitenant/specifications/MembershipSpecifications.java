package com.example.multitenant.specifications;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.models.Membership;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class MembershipSpecifications {
    public static Specification<Membership> isMember(boolean isMember) {
        return (Root<Membership> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("isMember"), isMember);
        };
    }

    public static Specification<Membership> hasFirstName(String firstName) {
        return (Root<Membership> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.like(root.get("user").get("firstName"), "%" + firstName + "%");
        };
    }

    public static Specification<Membership> hasLastName(String lastName) {
        return (Root<Membership> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.like(root.get("user").get("lastName"), "%" + lastName + "%");
        };
    }

    public static Specification<Membership> hasEmail(String email) {
        return (Root<Membership> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(cb.lower(root.get("user").get("email")), email.toLowerCase());
        };
    }

    public static Specification<Membership> hasJoinedBefore(LocalDateTime dateTime) {
        return (Root<Membership> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.lessThan(root.get("joinedAt"), dateTime);
        };
    }

    public static Specification<Membership> hasJoinedAfter(LocalDateTime dateTime) {
        return (Root<Membership> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.greaterThan(root.get("joinedAt"), dateTime);
        };
    }

    public static Specification<Membership> hasOrganizationId(Integer orgId) {
        return (Root<Membership> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("id").get("organizationId"), orgId);
        };
    }
}