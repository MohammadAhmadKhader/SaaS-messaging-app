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
@NoArgsConstructor
@Table(name = "organization_permission")
public class OrgPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false , unique = true, length = 64)
    private String name;

    @ManyToMany(mappedBy = "organizationPermissions")
    private List<OrgRole> roles = new ArrayList<>();

    @JsonProperty("isDefaultUser")
    @Column(name = "is_default_user", nullable = false)
    private Boolean isDefaultUser;

    @JsonProperty("isDefaultAdmin")
    @Column(name = "is_default_admin", nullable = false)
    private Boolean isDefaultAdmin;

    @JsonProperty("isDefaultOrgOwner")
    @Column(name = "is_default_org_owner", nullable = false)
    private Boolean isDefaultOrgOwner;

    public OrgPermissionWithRolesViewDTO toWithRoleViewDTO() {
        return new OrgPermissionWithRolesViewDTO(this);
    }

    public OrgPermissionViewDTO toViewDTO() {
        return new OrgPermissionViewDTO(this);
    }

    public OrgPermission(String name) {
        setName(name);
    }
}
