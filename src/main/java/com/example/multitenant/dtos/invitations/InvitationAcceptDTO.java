package com.example.multitenant.dtos.invitations;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
public class InvitationAcceptDTO {
    @NotNull(message = "invitation id can not be null")
    @Min(value = 1 ,message = "invitiation id can not be less than {value}")
    private Integer id;
}
