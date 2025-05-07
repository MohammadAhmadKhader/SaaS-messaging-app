package com.example.multitenant.dtos.users;

import com.example.multitenant.models.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String avatarUrl;

    public UserSearchDTO(User user) {
        setId(user.getId());
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setAvatarUrl(user.getAvatarUrl());
    }
}
