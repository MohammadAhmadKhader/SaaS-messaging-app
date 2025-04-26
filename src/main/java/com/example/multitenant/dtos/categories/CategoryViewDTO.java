package com.example.multitenant.dtos.categories;

import java.time.Instant;

import com.example.multitenant.models.Category;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryViewDTO {
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

