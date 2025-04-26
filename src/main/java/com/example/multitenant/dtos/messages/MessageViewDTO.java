package com.example.multitenant.dtos.messages;

import java.time.Instant;

import com.example.multitenant.dtos.users.UserMessageViewDTO;
import com.example.multitenant.models.Message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageViewDTO {
    private Integer id;
    private String content;
    private UserMessageViewDTO user;
    private Instant createdAt;
    private Instant updatedAt;

    public MessageViewDTO(Message message) {
        setId(message.getId());
        setContent(message.getContent());
        setUser(message.getSender().toUserMessageViewDTO());
        setCreatedAt(message.getCreatedAt());
        setUpdatedAt(message.getUpdatedAt());
    }
}