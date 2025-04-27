package com.example.multitenant.dtos.categories;

import java.io.Serializable;
import java.time.Instant;

import com.example.multitenant.models.Category;

import lombok.*;

// has "Serializable" for caching purposes
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Integer organizationId;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;

    public CategoryViewDTO(Category category) {
        setId(category.getId());
        setName(category.getName());
        setOrganizationId(category.getOrganizationId());
        setDisplayOrder(category.getDisplayOrder());
        setCreatedAt(category.getCreatedAt());
        setUpdatedAt(category.getUpdatedAt());
    }
}

