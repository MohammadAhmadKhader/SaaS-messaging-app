package com.example.demo.dtos.roles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dtos.permissions.PermissionViewDTO;
import com.example.demo.models.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private List<PermissionViewDTO> permissions = new ArrayList<>();

    public RoleViewDTO(Role role) {
        setId(role.getId());
        setName(role.getName());
        
        var permsView = role.getPermissions().stream().map((perm)->{
            return perm.toViewDTO();
        }).toList();

        setPermissions(permsView);
    }
}

