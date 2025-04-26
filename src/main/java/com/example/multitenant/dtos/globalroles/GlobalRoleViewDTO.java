package com.example.multitenant.dtos.globalroles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.multitenant.dtos.globalpermissions.GlobalPermissionViewDTO;
import com.example.multitenant.models.GlobalRole;

import lombok.*;

@Getter
@Setter
public class GlobalRoleViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private List<GlobalPermissionViewDTO> permissions = new ArrayList<>();

    public GlobalRoleViewDTO(GlobalRole globalRole) {
        setId(globalRole.getId());
        setName(globalRole.getName());
        
        var permsView = globalRole.getPermissions().stream().map((perm)->{
            return perm.toViewDTO();
        }).toList();

        setPermissions(permsView);
    }
}

