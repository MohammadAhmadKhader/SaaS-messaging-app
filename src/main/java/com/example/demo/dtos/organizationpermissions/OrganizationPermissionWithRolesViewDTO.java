package com.example.demo.dtos.organizationpermissions;

import java.io.Serializable;

import com.example.demo.models.OrganizationPermission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
