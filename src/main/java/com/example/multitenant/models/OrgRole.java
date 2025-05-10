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
    @Index(name = "idx_organization_roles_name_organizationid", columnList = "organization_id, name", unique = true),
})
public class OrgRole implements Serializable {
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
    private Set<OrgPermission> organizationPermissions = new HashSet<>();

    public OrgRoleViewDTO toViewDTO() {
        return new OrgRoleViewDTO(this);
    }

    public OrgRoleWithoutPermissionsDTO toViewWithoutPermissionsDTO() {
        return new OrgRoleWithoutPermissionsDTO(this);
    }

    public OrgRole(String name, String displayName) {
        setName(name);
        setDisplayName(displayName);
    }

    public OrgRole(String name) {
        setName(name);
    }
}
