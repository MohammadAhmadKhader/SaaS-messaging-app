package com.example.multitenant.dtos.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class LoginDTO {
    @NotNull(message = "email can not be empty")
    @Size(max = 64, message = "email must be at most {max}")
    @Size(min = 6, message = "email must be at least {min}")
    String email;

    @NotNull(message = "password can not be empty")
    @Size(max = 36, message = "password must be at most {max}")
    @Size(min = 6, message = "password must be at least {min}")
    String password;

}
