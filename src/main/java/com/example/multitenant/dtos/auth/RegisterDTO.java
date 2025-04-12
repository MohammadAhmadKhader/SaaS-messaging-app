package com.example.multitenant.dtos.auth;

import com.example.multitenant.models.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    @NotBlank(message = "email can not be empty")
    @Size(max = 64, message = "email must be at most {max}")
    @Size(min = 6, message = "email must be at least {min}")
    String email;

    @NotBlank(message = "firstName can not be empty")
    @Size(max = 64, message = "firstName must be at most {max}")
    @Size(min = 3, message = "firstName must be at least {min}")
    String firstName;

    @NotBlank(message = "lastName can not be empty")
    @Size(max = 64, message = "lastName must be at most {max}")
    @Size(min = 3, message = "lastName must be at least {min}")
    String lastName;

    @NotBlank(message = "password can not be empty")
    @Size(max = 36, message = "password must be at most {max}")
    @Size(min = 6, message = "password must be at least {min}")
    String password;

    public User toUser() {
        return new User(email, firstName, lastName ,password);
    }
}
