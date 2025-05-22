package com.example.multitenant.dtos.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class ResetPasswordDTO {
    @NotNull(message = "old password can not be empty")
    @Size(max = 36, message = "old password must be at most {max}")
    @Size(min = 6, message = "old password must be at least {min}")
    String oldPassword;

    @NotNull(message = "new password can not be empty")
    @Size(max = 36, message = "new password must be at most {max}")
    @Size(min = 6, message = "new password must be at least {min}")
    String newPassword;

    @NotNull(message = "confirm new password can not be empty")
    @Size(max = 36, message = "confirm new password must be at most {max}")
    @Size(min = 6, message = "confirm new password must be at least {min}")
    String confirmNewPassword;
}
