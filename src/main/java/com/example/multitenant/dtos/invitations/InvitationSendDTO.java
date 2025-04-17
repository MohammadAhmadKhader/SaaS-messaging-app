package com.example.multitenant.dtos.invitations;

import com.example.multitenant.models.Invitation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InvitationSendDTO {
    @NotNull(message = "recipient id is required")
    @Min(value = 1 ,message = "recipient can not be less than {value}")
    private Long recipientId;

    public Invitation toModel() {
        var inv = new Invitation();
        inv.setRecipientId(recipientId);
        return inv;
    }
}
