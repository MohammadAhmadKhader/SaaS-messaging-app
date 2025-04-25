package com.example.multitenant.services.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.messages.MessageDeleteDTO;
import com.example.multitenant.dtos.messages.MessageViewDTO;
import com.example.multitenant.dtos.websocket.WebSocketMessage;
import com.example.multitenant.models.Message;
import com.example.multitenant.models.enums.MessageAction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    private String getTenantTopic(Integer tenantId) {
        return "/topic/tenants/" + tenantId;
    }

    public void publishNewMessage(Message message, Integer tenantId) {
        var topic = getTenantTopic(tenantId);

        var wsMsg = new WebSocketMessage<MessageViewDTO>();
        wsMsg.setPayload(message.toViewDTO());
        wsMsg.setEvent(MessageAction.CREATE.name());

        this.messagingTemplate.convertAndSend(topic, wsMsg);
    }

    public void publishUpdatedMessage(Message message, Integer tenantId) {
        var topic = getTenantTopic(tenantId);
        
        var wsMsg = new WebSocketMessage<MessageViewDTO>();
        wsMsg.setPayload(message.toViewDTO());
        wsMsg.setEvent(MessageAction.UPDATE.name());

        this.messagingTemplate.convertAndSend(topic, wsMsg);
    }

    public void publishDeletedMessage(Integer messageId, Integer tenantId) {
        var topic = getTenantTopic(tenantId);

        var wsMsg = new WebSocketMessage<MessageDeleteDTO>();
        wsMsg.setPayload(new MessageDeleteDTO(tenantId));
        wsMsg.setEvent(MessageAction.DELETE.name());
        
        this.messagingTemplate.convertAndSend(topic, wsMsg);
    }
}