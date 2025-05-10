package com.example.multitenant.dtos.organizationpermissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.models.OrgPermission;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class OrgPermissionViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String name;

    public OrgPermissionViewDTO(OrgPermission perm) {
        setId(perm.getId());
        setName(perm.getName());
    }
}
