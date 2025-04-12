package com.example.demo.dtos.roles;

import com.example.demo.models.OrganizationRole;

import lombok.Getter;
import lombok.Setter;

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
