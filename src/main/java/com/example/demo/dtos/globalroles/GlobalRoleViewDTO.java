package com.example.demo.dtos.globalroles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dtos.globalpermissions.GlobalPermissionViewDTO;
import com.example.demo.models.GlobalRole;

import lombok.Getter;
import lombok.Setter;

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

