package com.example.multitenant.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.dtos.organizationpermissions.OrganizationPermissionViewDTO;
import com.example.multitenant.dtos.organizationpermissions.OrganizationPermissionWithRolesViewDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
    private boolean isDefaultUser;

    @JsonProperty("isDefaultAdmin")
    @Column(name = "is_default_admin")
    private boolean isDefaultAdmin;

    @JsonProperty("isDefaultSuperAdmin")
    @Column(name = "is_default_superAdmin")
    private boolean isDefaultSuperAdmin;

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
