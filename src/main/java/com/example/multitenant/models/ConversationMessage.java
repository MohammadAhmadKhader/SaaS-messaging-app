package com.example.multitenant.models;

import com.example.multitenant.dtos.conversationmessages.ConversationMessageViewDTO;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "conversation_messages", indexes = {
    @Index(name = "idx_conversation_message_conversation_id", columnList = "conversation_id"),
    @Index(name = "idx_conversation_message_sender_id", columnList = "sender_id")
})
public class ConversationMessage extends BaseMessage {

    @Column(name = "conversation_id", updatable = false, insertable = false)
    private Integer conversationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    
    @PrePersist
    private void loadDefaults() {
        this.setIsUpdated(false);
    }

    public ConversationMessageViewDTO toViewDTO() {
        return new ConversationMessageViewDTO(this);
    }
}
