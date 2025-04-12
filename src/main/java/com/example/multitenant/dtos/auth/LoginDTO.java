package com.example.multitenant.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @NotBlank(message = "email can not be empty")
    @Size(max = 64, message = "email must be at most {max}")
    @Size(min = 6, message = "email must be at least {min}")
    String email;

    @NotBlank(message = "password can not be empty")
    @Size(max = 36, message = "password must be at most {max}")
    @Size(min = 6, message = "password must be at least {min}")
    String password;

}
