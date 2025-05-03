package com.example.multitenant.dtos.users;

import com.example.multitenant.models.User;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserMessageViewDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String avatarUrl;

    public UserMessageViewDTO(User user) {
        setId(user.getId());
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setAvatarUrl(user.getAvatarUrl());
    }
}
