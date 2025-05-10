package com.example.multitenant.dtos.organizationpermissions;

import java.io.Serializable;

import com.example.multitenant.models.OrgPermission;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class OrgPermissionWithRolesViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String name;

    public OrgPermissionWithRolesViewDTO(OrgPermission perm) {
        setId(perm.getId());
        setName(perm.getName());
    }
}
