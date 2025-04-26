package com.example.multitenant.dtos.users;

import com.example.multitenant.models.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.*;

public record UserCreateDTO(
    @NotBlank(message = "email can not be empty")
    @Size(max = 64, message = "email must be at most {max}")
    @Size(min = 6, message = "email must be at least {min}")
    @Email(message = "invalid email")
    String email,

    @NotBlank(message = "firstName can not be empty")
    @Size(max = 64, message = "firstName must be at most {max}")
    @Size(min = 3, message = "firstName must be at least {min}")
    String firstName,

    @NotBlank(message = "lastName can not be empty")
    @Size(max = 64, message = "lastName must be at most {max}")
    @Size(min = 3, message = "lastName must be at least {min}")
    String lastName,

    @NotBlank(message = "password can not be empty")
    @Size(max = 36, message = "password must be at most {max}")
    @Size(min = 6, message = "password must be at least {min}")
    String password
){
    public User toModel() {
        return new User(this.email, this.firstName, this.lastName,this.password);
    }
}


