package com.example.demo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dtos.roles.RoleViewDTO;
import com.example.demo.dtos.roles.RoleWithoutPermissionsDTO;
import com.example.demo.dtos.shared.IViewDTO;

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
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role implements IViewDTO<RoleViewDTO>, Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name", nullable = false)
    String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "roles_permissions",
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "roles"),
        inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id", table = "permissions")
    )
    private List<Permission> permissions = new ArrayList<>();

    public RoleViewDTO toViewDTO() {
        return new RoleViewDTO(this);
    }

    public RoleWithoutPermissionsDTO toViewWithoutPermissionsDTO() {
        return new RoleWithoutPermissionsDTO(this);
    }

    public Role(String name) {
        setName(name);
    }

    public Role() {
        
    }
}
