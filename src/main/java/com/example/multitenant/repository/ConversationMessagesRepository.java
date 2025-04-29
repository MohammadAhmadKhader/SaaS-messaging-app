package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.ConversationMessage;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ConversationMessagesRepository extends GenericRepository<ConversationMessage, Integer> {
    @Query("""
            SELECT c FROM ConversationMessage c
            LEFT JOIN FETCH c.sender
            LEFT JOIN FETCH c.conversation conv
            LEFT JOIN FETCH conv.user1
            LEFT JOIN FETCH conv.user2
            WHERE (c.id = :id AND c.conversationId = :conversationId)
    """)
    public ConversationMessage findMessageByIdAndConvIdWithConvAndUsers(@Param("id") Integer id, @Param("conversationId") Integer conversationId);
}
