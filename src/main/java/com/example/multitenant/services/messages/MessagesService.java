package com.example.multitenant.services.messages;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.Message;
import com.example.multitenant.repository.MessagesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessagesService {
    private final MessagesRepository messagesRepository;

    public List<Message> findAllForUser(Integer userId) {
        return this.messagesRepository.findBySenderId(userId);
    }

    public Message create(Message message, Integer channelId, Integer orgId) {
        message.setChannelId(channelId);
        message.setOrganizationId(orgId);
        return this.messagesRepository.save(message);
    }

    public Message updateContent(Integer id, String content, Long userId) {
        var msg = this.messagesRepository.findByIdAndSenderId(id, userId);
        if (msg != null) {
            msg.setContent(content);
            if(!msg.getIsUpdated()) {
                msg.setIsUpdated(true);
            }

            return this.messagesRepository.save(msg);
        }
        
        throw new ResourceNotFoundException("message", id);
    }

    public void deleteUserMessage(Integer id, Long userId) {
        this.messagesRepository.deleteByIdAndSenderId(id, userId);
    }
}
