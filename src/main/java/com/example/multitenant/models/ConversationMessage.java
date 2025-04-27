package com.example.multitenant.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "conversation_messages")
public class ConversationMessage extends Message {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Conversation conversation;
}
