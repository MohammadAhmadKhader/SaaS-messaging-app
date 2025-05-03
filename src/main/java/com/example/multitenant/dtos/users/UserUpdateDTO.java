package com.example.multitenant.dtos.users;

import org.springframework.web.multipart.MultipartFile;

import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;
import com.example.multitenant.models.User;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AtLeastOneNotNull(fields = {"firstName", "lastName", "email"})
public class UserUpdateDTO {
    @Size(max = 64, message = "lastName must be at most {max}")
    @Size(min = 3, message = "lastName must be at least {min}")
    private String lastName;

    @Size(max = 64, message = "firstName must be at most {max}")
    @Size(min = 3, message = "firstName must be at least {min}")
    private String firstName;

    @Email(message = "invalid email address")
    @Size(max = 64, message = "email must be at most {max}")
    @Size(min = 6, message = "email must be at least {min}")
    private String email;

    private MultipartFile avatar;

    public User toModel() {
        return new User(email, firstName, lastName, null);
    }
}
