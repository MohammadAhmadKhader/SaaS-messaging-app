package com.example.multitenant.models;

import com.example.multitenant.dtos.conversationmessages.ConversationMessageViewDTO;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "conversation_messages")
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMessage extends Message {

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
