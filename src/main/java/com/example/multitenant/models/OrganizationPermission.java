package com.example.multitenant.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.dtos.organizationpermissions.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "organization_permission")
public class OrganizationPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "organizationPermissions")
    private List<OrganizationRole> roles = new ArrayList<>();

    @JsonProperty("isDefaultUser")
    @Column(name = "is_default_user")
    private Boolean isDefaultUser;

    @JsonProperty("isDefaultAdmin")
    @Column(name = "is_default_admin")
    private Boolean isDefaultAdmin;

    @JsonProperty("isDefaultOrgOwner")
    @Column(name = "is_default_org_owner")
    private Boolean isDefaultOrgOwner;

    public OrganizationPermissionWithRolesViewDTO toWithRoleViewDTO() {
        return new OrganizationPermissionWithRolesViewDTO(this);
    }

    public OrganizationPermissionViewDTO toViewDTO() {
        return new OrganizationPermissionViewDTO(this);
    }

    public OrganizationPermission(String name) {
        setName(name);
    }
    
    public OrganizationPermission() {
        
    }
}
