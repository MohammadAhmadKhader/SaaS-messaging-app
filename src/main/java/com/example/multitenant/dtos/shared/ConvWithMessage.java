package com.example.multitenant.dtos.shared;

import com.example.multitenant.models.Conversation;
import com.example.multitenant.models.ConversationMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConvWithMessage {
    private ConversationMessage message;
    private Conversation conversation;

    public ConvWithMessage(ConversationMessage message, Conversation conversation) {
        setConversation(conversation);
        setMessage(message);
    }
    
    public static ConvWithMessage of(ConversationMessage message, Conversation conversation) {
        return new ConvWithMessage(message, conversation);
    }
}
