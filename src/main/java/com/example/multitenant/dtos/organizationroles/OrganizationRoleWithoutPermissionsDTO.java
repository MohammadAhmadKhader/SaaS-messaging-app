package com.example.multitenant.dtos.organizationroles;

import com.example.multitenant.models.OrganizationRole;

import lombok.*;

@Getter
@Setter
public class OrganizationRoleWithoutPermissionsDTO {
    private Integer id;

    private String name;

    public OrganizationRoleWithoutPermissionsDTO(OrganizationRole role) {
        setId(role.getId());
        setName(role.getName());
    }
}
