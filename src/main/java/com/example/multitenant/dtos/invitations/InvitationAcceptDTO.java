package com.example.multitenant.dtos.invitations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InvitationAcceptDTO {
    @NotNull(message = "invitation id can not be null")
    @Min(value = 1 ,message = "invitiation id can not be less than {value}")
    private Integer id;
}
