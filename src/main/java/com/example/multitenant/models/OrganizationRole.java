package com.example.multitenant.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.multitenant.dtos.organizationroles.*;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "organization_roles",indexes = {
    @Index(name = "idx_name_organizationid", columnList = "name, organization_id", unique = true)
})
public class OrganizationRole implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name", nullable = false, length = 64)
    String name;

    @Column(name = "display_name", nullable = false, length = 64)
    String displayName;

    @Column(name = "organization_id", nullable = true)
    Integer organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    Organization organization;    

    @ManyToMany(mappedBy = "organizationRoles")
    private List<Membership> memberships;

    @ManyToMany(fetch = FetchType.LAZY)
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

    public OrganizationRole(String name, String displayName) {
        setName(name);
        setDisplayName(displayName);
    }

    public OrganizationRole(String name) {
        setName(name);
    }
}
