package com.example.multitenant.dtos.organizationroles;

import java.io.Serializable;

import com.example.multitenant.models.OrgRole;

import lombok.*;

@Getter
@Setter
@ToString
public class OrgRoleCacheDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String name;

    public OrgRoleCacheDTO(OrgRole organizationRole) {
        setId(organizationRole.getId());
        setName(organizationRole.getName());
    }
}
