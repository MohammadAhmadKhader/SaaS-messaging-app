package com.example.multitenant.repository;

import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Conversation;

@Repository
public interface ConversationsRepository extends GenericRepository<Conversation, Integer> {

}