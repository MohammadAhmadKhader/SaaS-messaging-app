package com.example.multitenant.specifications;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.example.multitenant.models.Content;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class ContentSpecifications {
    public static Specification<Content> hasStatus(String status) {
        return (Root<Content> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Content> hasContentType(String contentType) {
        return (Root<Content> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("contentType"), contentType);
        };
    }

    public static Specification<Content> hasTitle(String title) {
        return (Root<Content> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.like(root.get("title"), "%" + title + "%");
        };
    }

    public static Specification<Content> hasCreatedAt(String createdAt) {
        return (Root<Content> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("createdAt"), LocalDateTime.parse(createdAt));
        };
    }

    public static Specification<Content> hasOrganizationId(Integer orgId) {
        return (Root<Content> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {
            return cb.equal(root.get("organizationId"), orgId);
        };
    }
}

