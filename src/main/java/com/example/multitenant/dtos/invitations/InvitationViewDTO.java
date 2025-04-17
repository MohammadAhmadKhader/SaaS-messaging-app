package com.example.multitenant.dtos.invitations;

import java.time.Instant;

import com.example.multitenant.models.Invitation;
import com.example.multitenant.models.enums.InvitationStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationViewDTO {
    private Integer id;
    private InvitationStatus status;
    private Long recipientId;
    private Instant createdAt;

    public InvitationViewDTO(Invitation inv) {
        setId(inv.getId());
        setStatus(inv.getStatus());
        setRecipientId(inv.getRecipientId());
        setCreatedAt(inv.getCreatedAt());
    }
}
