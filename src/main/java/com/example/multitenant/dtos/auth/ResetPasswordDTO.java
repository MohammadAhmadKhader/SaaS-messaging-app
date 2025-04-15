package com.example.multitenant.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ResetPasswordDTO {
    @NotBlank(message = "old password can not be empty")
    @Size(max = 36, message = "old password must be at most {max}")
    @Size(min = 6, message = "old password must be at least {min}")
    String oldPassword;

    @NotBlank(message = "new password can not be empty")
    @Size(max = 36, message = "new password must be at most {max}")
    @Size(min = 6, message = "new password must be at least {min}")
    String newPassword;

    @NotBlank(message = "confirm new password can not be empty")
    @Size(max = 36, message = "confirm new password must be at most {max}")
    @Size(min = 6, message = "confirm new password must be at least {min}")
    String confirmNewPassword;
}
