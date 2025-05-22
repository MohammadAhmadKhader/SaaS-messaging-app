package com.example.multitenant.dtos.auth;

import com.example.multitenant.models.User;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class RegisterDTO {
    @NotNull(message = "email can not be empty")
    @Size(max = 64, message = "email must be at most {max}")
    @Size(min = 6, message = "email must be at least {min}")
    String email;

    @NotNull(message = "firstName can not be empty")
    @Size(max = 64, message = "firstName must be at most {max}")
    @Size(min = 3, message = "firstName must be at least {min}")
    String firstName;

    @NotNull(message = "lastName can not be empty")
    @Size(max = 64, message = "lastName must be at most {max}")
    @Size(min = 3, message = "lastName must be at least {min}")
    String lastName;

    @NotNull(message = "password can not be empty")
    @Size(max = 36, message = "password must be at most {max}")
    @Size(min = 6, message = "password must be at least {min}")
    String password;

    public User toUser() {
        return new User(email, firstName, lastName ,password);
    }

    public String getEmail() {
        return this.email.toLowerCase();
    }
}
