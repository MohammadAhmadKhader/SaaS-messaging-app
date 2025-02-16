package com.example.demo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dtos.permissions.PermissionViewDTO;
import com.example.demo.dtos.permissions.PermissionWithRolesViewDTO;
import com.example.demo.dtos.shared.IViewDTO;
import com.example.demo.dtos.users.UserViewDTO;

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
@Table(name = "permissions")
public class Permission implements IViewDTO<PermissionViewDTO>, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles = new ArrayList<>();

    public PermissionWithRolesViewDTO toWithRoleViewDTO() {
        return new PermissionWithRolesViewDTO(this);
    }

    @Override
    public PermissionViewDTO toViewDTO() {
        return new PermissionViewDTO(this);
    }

    public Permission(String name) {
        setName(name);
    }
    
    public Permission() {
        
    }
}
