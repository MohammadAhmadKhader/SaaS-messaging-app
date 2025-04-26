package com.example.multitenant.dtos.users;

import java.io.Serializable;
import java.time.Instant;

import com.example.multitenant.models.User;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class UserWithoutRolesViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;

    public UserWithoutRolesViewDTO(User user) {
        setId(user.getId());
        setEmail(user.getEmail());
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setCreatedAt(user.getCreatedAt());
        setUpdatedAt(user.getUpdatedAt());
    }
}