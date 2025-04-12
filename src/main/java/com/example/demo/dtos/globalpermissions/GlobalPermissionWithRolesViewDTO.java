package com.example.demo.dtos.globalpermissions;

import java.io.Serializable;

import com.example.demo.models.GlobalPermission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GlobalPermissionWithRolesViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String name;

    public GlobalPermissionWithRolesViewDTO(GlobalPermission perm) {
        setId(perm.getId());
        setName(perm.getName());
    }
}
