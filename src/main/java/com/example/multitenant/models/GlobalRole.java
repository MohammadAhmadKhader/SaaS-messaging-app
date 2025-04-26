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
@Table(name = "global_roles")
public class GlobalRole implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name", nullable = false)
    String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
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

    public GlobalRole(String name) {
        setName(name);
    }

    public GlobalRole() {
        
    }
}
