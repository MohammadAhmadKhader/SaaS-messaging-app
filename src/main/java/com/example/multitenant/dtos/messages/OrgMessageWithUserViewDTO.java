package com.example.multitenant.dtos.messages;

import java.time.Instant;

import com.example.multitenant.dtos.users.*;
import com.example.multitenant.models.Message;
import com.example.multitenant.models.OrgMessage;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class OrgMessageWithUserViewDTO {
    private Integer id;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private UserWithoutPermissionsViewDTO user;

    public OrgMessageWithUserViewDTO(OrgMessage message) {
        setId(message.getId());
        setContent(message.getContent());
        setCreatedAt(message.getCreatedAt());
        setUpdatedAt(message.getUpdatedAt());
        setUser(message.getSender().toUserWithoutPermissionsViewDTO());
    }
}
