package com.example.multitenant.dtos.organizationroles;

import java.io.Serializable;

import com.example.multitenant.models.OrganizationRole;

import lombok.*;

@Getter
@Setter
public class OrganizationRoleCacheDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String name;

    public OrganizationRoleCacheDTO(OrganizationRole organizationRole) {
        setId(organizationRole.getId());
        setName(organizationRole.getName());
    }
}
