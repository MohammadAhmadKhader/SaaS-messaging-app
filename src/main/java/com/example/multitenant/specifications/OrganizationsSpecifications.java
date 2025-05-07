package com.example.multitenant.specifications;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.models.Organization;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class OrganizationsSpecifications {
    public static Specification<Organization> hasName(String name) {
        return (Root<Organization> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }
}