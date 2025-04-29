package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Conversation;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ConversationsRepository extends GenericRepository<Conversation, Integer> {
    @Query("""
            SELECT c FROM Conversation c
            LEFT JOIN FETCH c.user1
            LEFT JOIN FETCH c.user2
            LEFT JOIN FETCH c.lastMessage
            WHERE c.id = :id
    """)
    Conversation findByIdWithLastMessageAndUsers(@Param("id") Integer id);
}