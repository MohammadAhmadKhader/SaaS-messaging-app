package com.example.multitenant.dtos.roles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.dtos.organizationpermissions.OrganizationPermissionViewDTO;
import com.example.multitenant.models.OrganizationRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationRoleViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private List<OrganizationPermissionViewDTO> organizationPermissions = new ArrayList<>();

    public OrganizationRoleViewDTO(OrganizationRole role) {
        setId(role.getId());
        setName(role.getName());
        
        var permsView = role.getOrganizationPermissions().stream().map((perm)->{
            return perm.toViewDTO();
        }).toList();

        setOrganizationPermissions(permsView);
    }
}

