package com.example.multitenant.dtos.users;

import org.springframework.web.multipart.MultipartFile;

import com.example.multitenant.common.validators.contract.AllowedFileTypes;
import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;
import com.example.multitenant.common.validators.contract.FileSize;
import com.example.multitenant.models.User;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AtLeastOneNotNull(fields = {"firstName", "lastName", "email", "avatar"})
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

    @AllowedFileTypes(
        types = { "image/jpeg", "image/png", "image/avif", "image/webp"},
        message = "only JPEG, PNG, AVIF, and WEBP images are allowed"
    )
    @FileSize(max = 1 * 1024 * 1024, message = "file must be 1MB or smaller")
    private MultipartFile avatar;

    public User toModel() {
        return new User(email, firstName, lastName, null);
    }
}
