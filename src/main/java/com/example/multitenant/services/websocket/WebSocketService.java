package com.example.multitenant.services.websocket;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.messages.*;
import com.example.multitenant.dtos.websocket.WebSocketMessage;
import com.example.multitenant.models.*;
import com.example.multitenant.models.enums.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    private String getTenantTopic(Integer tenantId, Integer categoryId) {
        return "/topic/tenants/" + tenantId + "/categories/" +categoryId;
    }

    public void publishNewMessage(OrgMessage message, Integer tenantId, Integer categoryId) {
        var topic = getTenantTopic(tenantId, categoryId);

        var wsMsg = new WebSocketMessage<OrgMessageViewDTO>();
        wsMsg.setPayload(message.toViewDTO());
        wsMsg.setEvent(MessageAction.CREATE.name());

        this.messagingTemplate.convertAndSend(topic, wsMsg);
    }

    public void publishUpdatedMessage(OrgMessage message, Integer tenantId, Integer categoryId) {
        var topic = getTenantTopic(tenantId, categoryId);
        
        var wsMsg = new WebSocketMessage<OrgMessageViewDTO>();
        wsMsg.setPayload(message.toViewDTO());
        wsMsg.setEvent(MessageAction.UPDATE.name());

        this.messagingTemplate.convertAndSend(topic, wsMsg);
    }

    public void publishDeletedMessage(Integer messageId, Integer tenantId, Integer categoryId) {
        var topic = getTenantTopic(tenantId, categoryId);

        var wsMsg = new WebSocketMessage<OrgMessageDeleteDTO>();
        wsMsg.setPayload(new OrgMessageDeleteDTO(tenantId));
        wsMsg.setEvent(MessageAction.DELETE.name());
        
        this.messagingTemplate.convertAndSend(topic, wsMsg);
    }
}