package com.example.multitenant.dtos.organizationroles;

import com.example.multitenant.models.OrgRole;

import lombok.*;

@Getter
@Setter
public class OrgRoleWithoutPermissionsDTO {
    private Integer id;
    private String name;
    private String displayName;

    public OrgRoleWithoutPermissionsDTO(OrgRole role) {
        setId(role.getId());
        setName(role.getName());
        setDisplayName(role.getDisplayName());
    }
}
