package com.example.multitenant.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.models.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class UsersSpecifications {
    public static Specification<User> hasFirstName(String firstName) {
        return (Root<User> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.like(root.get("firstName"), "%" + firstName + "%");
        };
    }

    public static Specification<User> hasLastName(String lastName) {
        return (Root<User> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.like(root.get("lastName"), "%" + lastName + "%");
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (Root<User> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(cb.lower(root.get("email")), email.toLowerCase());
        };
    }
}