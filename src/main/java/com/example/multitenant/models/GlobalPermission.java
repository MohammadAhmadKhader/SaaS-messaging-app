package com.example.multitenant.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.dtos.globalpermissions.GlobalPermissionViewDTO;
import com.example.multitenant.dtos.globalpermissions.GlobalPermissionWithRolesViewDTO;
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

@Setter
@Getter
@Entity
@Table(name = "global_permissions")
public class GlobalPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "permissions")
    private List<GlobalRole> roles = new ArrayList<>();

    @JsonProperty("isDefaultUser")
    @Column(name = "is_default_user")
    private Boolean isDefaultUser;

    @JsonProperty("isDefaultAdmin")
    @Column(name = "is_default_admin")
    private Boolean isDefaultAdmin;

    @JsonProperty("isDefaultSuperAdmin")
    @Column(name = "is_default_superAdmin")
    private Boolean isDefaultSuperAdmin;

    public GlobalPermissionWithRolesViewDTO toWithRoleViewDTO() {
        return new GlobalPermissionWithRolesViewDTO(this);
    }

    public GlobalPermissionViewDTO toViewDTO() {
        return new GlobalPermissionViewDTO(this);
    }

    public GlobalPermission(String name) {
        setName(name);
    }
    
    public GlobalPermission() {
        
    }
}
