package com.example.multitenant.services.conversations;

import org.springframework.data.domain.Example;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.Conversation;
import com.example.multitenant.models.ConversationMessage;
import com.example.multitenant.models.User;
import com.example.multitenant.repository.ConversationMessagesRepository;
import com.example.multitenant.repository.ConversationsRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ConversationsService {
    private final ConversationsRepository conversationsRepository;
    private final ConversationMessagesRepository conversationMessagesRepository;
    
    // creates a new conversation if there is none, if there is returns null
    public Conversation initConversation(User firstUser, User secondUser) {
        var existingConv = this.findConvWithSuchUsers(firstUser, secondUser);
        if(existingConv != null) {
            return null;
        }

        var conv = new Conversation();
        conv.setUser1(firstUser);
        conv.setUser2(secondUser);
        conv.setUsersInOrder();

        return this.conversationsRepository.save(conv);
    }

    public Conversation findConvWithSuchUsers(User firstUser, User secondUser) {
        var probe = new Conversation();
        probe.setUser1(firstUser);
        probe.setUser2(secondUser);
        probe.setUsersInOrder();

        return this.conversationsRepository.findOne(Example.of(probe)).orElse(null);
    }

    @Transactional
    public Conversation addMessageToConv(User sender, Integer conversationId, ConversationMessage newMsg) {
        var conversation = this.conversationsRepository.findByIdWithLastMessageAndUsers(conversationId);
        if(conversation == null) {
            throw new ResourceNotFoundException("conversation", conversationId);
        }

        newMsg.setConversation(conversation);
        newMsg.setSender(sender);
        
        this.conversationMessagesRepository.save(newMsg);
        conversation.setLastMessage(newMsg);
        
        var conv = this.conversationsRepository.save(conversation);
         
        return conv;
    }

    public ConversationMessage updateConvMessageContent(User sender, Integer messageId, Integer conversationId, String newContent) {
        var message = this.conversationMessagesRepository.findMessageByIdAndConvIdWithConvAndUsers(messageId, conversationId);
        if(message == null) {
            throw new ResourceNotFoundException("message", messageId);
        }

        if(!message.getSenderId().equals(sender.getId())) {
            throw new AuthorizationDeniedException("can not update a message that does not belong to you");
        }

        message.setContent(newContent);
        message.setIsUpdated(true);
        
        return this.conversationMessagesRepository.save(message);
    }

    public Conversation removeMessageFromConv(Integer messageId, Integer conversationId, User sender) {
        var msg = this.conversationMessagesRepository.findMessageByIdAndConvIdWithConvAndUsers(messageId, conversationId);
        if(!msg.getSenderId().equals(sender.getId())) {
            throw new AuthorizationDeniedException("user can not remove a message does not belong to him");
        }

        var conv = msg.getConversation();
        // this because if we dont do it, hibernate will throw error "persistent instance references an unsaved transient instance of User"
        msg.setSender(null);
        this.conversationMessagesRepository.delete(msg);

        return conv;
    }
}