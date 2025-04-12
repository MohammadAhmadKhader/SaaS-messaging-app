package com.example.multitenant.dtos.users;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import com.example.multitenant.dtos.globalroles.GlobalRoleViewDTO;
import com.example.multitenant.models.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
    private List<GlobalRoleViewDTO> roles;

    public UserViewDTO(User user) {
        setId(user.getId());
        setEmail(user.getEmail());
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setRoles(user.getRoles().stream().map((role) -> {
            return role.toViewDTO();
        }).toList());
        setCreatedAt(user.getCreatedAt());
        setUpdatedAt(user.getUpdatedAt());
    }
}
