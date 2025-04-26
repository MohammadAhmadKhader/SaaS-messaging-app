package com.example.multitenant.dtos.organizationpermissions;

import java.io.Serializable;

import com.example.multitenant.models.OrganizationPermission;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class OrganizationPermissionWithRolesViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String name;

    public OrganizationPermissionWithRolesViewDTO(OrganizationPermission perm) {
        setId(perm.getId());
        setName(perm.getName());
    }
}
