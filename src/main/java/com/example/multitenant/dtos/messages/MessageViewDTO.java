package com.example.multitenant.dtos.messages;

import java.time.Instant;

import com.example.multitenant.models.Message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageViewDTO {
    private Integer id;
    private String content;
    private Long senderId;
    private Instant createdAt;
    private Instant updatedAt;

    public MessageViewDTO(Message message) {
        setId(message.getId());
        setContent(message.getContent());
        setSenderId(message.getSenderId());
        setCreatedAt(message.getCreatedAt());
        setUpdatedAt(message.getUpdatedAt());
    }
}