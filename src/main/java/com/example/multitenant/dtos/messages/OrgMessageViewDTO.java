package com.example.multitenant.dtos.messages;

import java.time.Instant;

import com.example.multitenant.dtos.users.*;
import com.example.multitenant.models.OrgMessage;

import lombok.*;

@Getter
@Setter
public class OrgMessageViewDTO {
    private Integer id;
    private String content;
    private UserMessageViewDTO user;
    private Instant createdAt;
    private Instant updatedAt;

    public OrgMessageViewDTO(OrgMessage message) {
        setId(message.getId());
        setContent(message.getContent());
        setUser(message.getSender().toUserMessageViewDTO());
        setCreatedAt(message.getCreatedAt());
        setUpdatedAt(message.getUpdatedAt());
    }
}