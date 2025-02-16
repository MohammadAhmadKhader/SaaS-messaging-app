package com.example.demo.dtos.roles;

import com.example.demo.models.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleWithoutPermissionsDTO {
    private Integer id;

    private String name;

    public RoleWithoutPermissionsDTO(Role role) {
        setId(role.getId());
        setName(role.getName());
    }
}
