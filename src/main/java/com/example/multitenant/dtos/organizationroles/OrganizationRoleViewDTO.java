package com.example.multitenant.dtos.organizationroles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.dtos.organizationpermissions.*;
import com.example.multitenant.models.OrganizationRole;

import lombok.*;

@Getter
@Setter
public class OrganizationRoleViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String displayName;
    private List<OrganizationPermissionViewDTO> permissions = new ArrayList<>();

    public OrganizationRoleViewDTO(OrganizationRole role) {
        setId(role.getId());
        setName(role.getName());
        
        var permsView = role.getOrganizationPermissions().stream().map((perm)->{
            return perm.toViewDTO();
        }).toList();

        setPermissions(permsView);
        setDisplayName(role.getDisplayName());
    }
}

