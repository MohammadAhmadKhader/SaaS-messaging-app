package com.example.multitenant.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.dtos.roles.OrganizationRoleViewDTO;
import com.example.multitenant.dtos.roles.OrganizationRoleWithoutPermissionsDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "organization_roles")
public class OrganizationRole implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "organization_id", nullable = true)
    Integer organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    Organization organization;    

    @ManyToMany(mappedBy = "organizationRoles")
    private List<OrganizationMembership> memberships = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "organization_roles_organization_permissions",
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "organization_roles"),
        inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id", table = "organization_permissions")
    )
    private List<OrganizationPermission> organizationPermissions = new ArrayList<>();

    public OrganizationRoleViewDTO toViewDTO() {
        return new OrganizationRoleViewDTO(this);
    }

    public OrganizationRoleWithoutPermissionsDTO toViewWithoutPermissionsDTO() {
        return new OrganizationRoleWithoutPermissionsDTO(this);
    }

    public OrganizationRole(String name) {
        setName(name);
    }

    public OrganizationRole() {
        
    }
}
