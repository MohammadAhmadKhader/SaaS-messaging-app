package com.example.multitenant.dtos.globalpermissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.models.GlobalPermission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GlobalPermissionViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String name;

    public GlobalPermissionViewDTO(GlobalPermission perm) {
        setId(perm.getId());
        setName(perm.getName());
    }
}
