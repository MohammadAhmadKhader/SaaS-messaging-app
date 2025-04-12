package com.example.demo.dtos.users;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.demo.dtos.globalroles.GlobalRoleWithoutPermissionsDTO;
import com.example.demo.models.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithoutPermissionsViewDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
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

        setCreatedAt(user.getCreatedAt());
        setUpdatedAt(user.getUpdatedAt());
    }
}
