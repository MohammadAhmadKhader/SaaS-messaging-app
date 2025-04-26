package com.example.multitenant.dtos.organizationpermissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.models.OrganizationPermission;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class OrganizationPermissionViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String name;

    public OrganizationPermissionViewDTO(OrganizationPermission perm) {
        setId(perm.getId());
        setName(perm.getName());
    }
}
