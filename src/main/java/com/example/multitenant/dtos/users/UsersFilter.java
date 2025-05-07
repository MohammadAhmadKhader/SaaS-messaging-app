package com.example.multitenant.dtos.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersFilter {
    private String firstName;
    private String lastName;
    private String email;
}
