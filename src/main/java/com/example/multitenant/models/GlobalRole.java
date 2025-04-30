package com.example.multitenant.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.dtos.globalroles.*;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "global_roles")
public class GlobalRole implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name", nullable = false, length = 64)
    String name;

    @Column(name = "displayName", nullable = false, length = 64)
    String displayName;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "global_roles_global_permissions",
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "global_roles"),
        inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id", table = "global_permissions")
    )
    @OrderBy("id ASC")
    private List<GlobalPermission> permissions = new ArrayList<>();

    public GlobalRoleViewDTO toViewDTO() {
        return new GlobalRoleViewDTO(this);
    }

    public GlobalRoleWithoutPermissionsDTO toViewWithoutPermissionsDTO() {
        return new GlobalRoleWithoutPermissionsDTO(this);
    }

    public GlobalRole(String name,String displayName) {
        setName(name);
        setDisplayName(displayName);
    }
}
