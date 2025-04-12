package com.example.multitenant.dtos.globalroles;

import com.example.multitenant.models.GlobalRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalRoleWithoutPermissionsDTO {
    private Integer id;

    private String name;

    public GlobalRoleWithoutPermissionsDTO(GlobalRole globalRole) {
        setId(globalRole.getId());
        setName(globalRole.getName());
    }
}
