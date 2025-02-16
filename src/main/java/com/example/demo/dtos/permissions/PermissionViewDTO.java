package com.example.demo.dtos.permissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.models.Permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PermissionViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String name;

    public PermissionViewDTO(Permission perm) {
        setId(perm.getId());
        setName(perm.getName());
    }
}
