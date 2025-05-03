package com.example.multitenant.dtos.users;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.multitenant.dtos.globalroles.GlobalRoleWithoutPermissionsDTO;
import com.example.multitenant.models.User;

import lombok.*;

@Getter
@Setter
public class UserWithoutPermissionsViewDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private List<GlobalRoleWithoutPermissionsDTO> roles;

    public UserWithoutPermissionsViewDTO(User user) {
        setId(user.getId());
        setEmail(user.getEmail());
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());

        var idsMap = new HashMap<Integer, Object>();
        var rolesView = new ArrayList<GlobalRoleWithoutPermissionsDTO>();
        user.getRoles().stream().forEach((role) -> {
            if(!idsMap.containsKey(role.getId())) {
                idsMap.put(role.getId(), "");
                rolesView.add(role.toViewWithoutPermissionsDTO());
            }
        });
        setRoles(rolesView);
        setAvatarUrl(user.getAvatarUrl());
        
        setCreatedAt(user.getCreatedAt());
        setUpdatedAt(user.getUpdatedAt());
    }
}
