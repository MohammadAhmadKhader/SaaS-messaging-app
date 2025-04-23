package com.example.multitenant.dtos.invitations;

import com.example.multitenant.models.enums.InvitationStatus;
import com.example.multitenant.models.enums.InvitiationAction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InvitationCancelRejectDTO {
    @NotNull(message = "action is required")
    private InvitiationAction action;

    @NotNull(message = "recipient id is required")
    @Min(value = 1 ,message = "recipient id can not be less than {value}")
    private Long recipientId;
}
