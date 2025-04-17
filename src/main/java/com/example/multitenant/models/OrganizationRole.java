package com.example.multitenant.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.multitenant.dtos.organizationroles.OrganizationRoleViewDTO;
import com.example.multitenant.dtos.organizationroles.OrganizationRoleWithoutPermissionsDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "organization_roles",indexes = {
    @Index(name = "idx_name_organizationid", columnList = "name, organization_id", unique = true)
})
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
    private List<Membership> memberships;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "organization_roles_organization_permissions",
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "organization_roles"),
        inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id", table = "organization_permissions")
    )
    @OrderBy("id ASC")
    private Set<OrganizationPermission> organizationPermissions = new HashSet<>();

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
