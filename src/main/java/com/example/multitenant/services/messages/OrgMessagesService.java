package com.example.multitenant.services.messages;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.OrgMessage;
import com.example.multitenant.repository.OrgMessagesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrgMessagesService {
    private final OrgMessagesRepository orgMessagesRepository;

    public List<OrgMessage> findAllForUser(Integer userId) {
        return this.orgMessagesRepository.findBySenderId(userId);
    }

    public OrgMessage create(OrgMessage message, Integer channelId, Integer orgId) {
        message.setChannelId(channelId);
        message.setOrganizationId(orgId);
        return this.orgMessagesRepository.save(message);
    }

    public OrgMessage updateContent(Integer id, String content, Long userId) {
        var msg = this.orgMessagesRepository.findByIdAndSenderId(id, userId);
        if (msg != null) {
            msg.setContent(content);
            if(!msg.getIsUpdated()) {
                msg.setIsUpdated(true);
            }

            return this.orgMessagesRepository.save(msg);
        }
        
        throw new ResourceNotFoundException("message", id);
    }

    public void deleteUserMessage(Integer id, Long userId) {
        this.orgMessagesRepository.deleteByIdAndSenderId(id, userId);
    }
}
