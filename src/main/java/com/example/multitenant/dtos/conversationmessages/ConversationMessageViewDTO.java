package com.example.multitenant.dtos.conversationmessages;

import java.time.Instant;

import com.example.multitenant.dtos.users.UserMessageViewDTO;
import com.example.multitenant.models.ConversationMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationMessageViewDTO {
    private Integer id;
    private String content;
    private UserMessageViewDTO user;
    private Instant createdAt;
    private Instant updatedAt;

    public ConversationMessageViewDTO(ConversationMessage message) {
        setId(message.getId());
        setUser(message.getSender().toUserMessageViewDTO()); // this here throws the errror
        setCreatedAt(message.getCreatedAt());
        setUpdatedAt(message.getUpdatedAt());
    }
}
