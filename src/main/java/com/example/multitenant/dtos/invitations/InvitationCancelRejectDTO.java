package com.example.multitenant.dtos.invitations;

import com.example.multitenant.models.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
public class InvitationCancelRejectDTO {
    @NotNull(message = "action is required")
    private InvitiationAction action;

    @NotNull(message = "recipient id is required")
    @Min(value = 1 ,message = "recipient id can not be less than {value}")
    private Long recipientId;
}
