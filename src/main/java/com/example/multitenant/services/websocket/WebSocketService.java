package com.example.multitenant.services.websocket;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.conversationmessages.ConversationMessageDeleteDTO;
import com.example.multitenant.dtos.conversationmessages.ConversationMessageViewDTO;
import com.example.multitenant.dtos.messages.*;
import com.example.multitenant.dtos.websocket.WebSocketMessage;
import com.example.multitenant.models.*;
import com.example.multitenant.models.enums.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    private String getTenantTopic(Integer tenantId, Integer categoryId) {
        return "/topic/tenants/" + tenantId + "/categories/" +categoryId;
    }

    private String getUserNotificationsQueue() {
        // frontend will listen to "/user/queue/notifications", we not using "user" explicitly 
        // because we are going to use method "convertAndSendToUser"
        return "/queue/notifications";
    }

    private String getOrgEventName(String event) {
        return "org:" +event;
    }

    private String getConvEventName(String event) {
        return "conv:" +event;
    }

    public void publishNewOrgMessage(OrgMessage message, Integer tenantId, Integer categoryId) {
        var topic = getTenantTopic(tenantId, categoryId);

        var wsMsg = new WebSocketMessage<OrgMessageViewDTO>();
        wsMsg.setPayload(message.toViewDTO());
        wsMsg.setEvent(getOrgEventName(MessageAction.CREATE.name()));

        this.messagingTemplate.convertAndSend(topic, wsMsg);
    }

    public void publishUpdatedOrgMessage(OrgMessage message, Integer tenantId, Integer categoryId) {
        var topic = getTenantTopic(tenantId, categoryId);
        
        var wsMsg = new WebSocketMessage<OrgMessageViewDTO>();
        wsMsg.setPayload(message.toViewDTO());
        wsMsg.setEvent(getOrgEventName(MessageAction.UPDATE.name()));

        this.messagingTemplate.convertAndSend(topic, wsMsg);
    }

    public void publishDeletedOrgMessage(Integer messageId, Integer tenantId, Integer categoryId) {
        var topic = getTenantTopic(tenantId, categoryId);

        var wsMsg = new WebSocketMessage<OrgMessageDeleteDTO>();
        wsMsg.setPayload(new OrgMessageDeleteDTO(tenantId));
        wsMsg.setEvent(getOrgEventName(MessageAction.DELETE.name()));
        
        this.messagingTemplate.convertAndSend(topic, wsMsg);
    }

    public void publishNewConvMessage(ConversationMessage message, User target) {
        var dest = getUserNotificationsQueue();
        
        var wsMsg = new WebSocketMessage<ConversationMessageViewDTO>();
        wsMsg.setPayload(message.toViewDTO());
        wsMsg.setEvent(getConvEventName(MessageAction.CREATE.name()));

        this.messagingTemplate.convertAndSendToUser(
            target.getEmail(), 
            dest,
            wsMsg
        );
    }

    public void publishUpdateConvMessage(ConversationMessage message, User target) {
        var dest = getUserNotificationsQueue();

        var wsMsg = new WebSocketMessage<ConversationMessageViewDTO>();
        wsMsg.setPayload(message.toViewDTO());
        wsMsg.setEvent(getConvEventName(MessageAction.UPDATE.name()));

        this.messagingTemplate.convertAndSendToUser(
            target.getEmail(), 
            dest,
            wsMsg
        );
    }

    public void publishDeleteConvMessage(Integer messageId, User target) {
        var dest = getUserNotificationsQueue();

        var wsMsg = new WebSocketMessage<ConversationMessageDeleteDTO>();
        wsMsg.setPayload(new ConversationMessageDeleteDTO(messageId));
        wsMsg.setEvent(getConvEventName(MessageAction.DELETE.name()));

        this.messagingTemplate.convertAndSendToUser(
            target.getEmail(), 
            dest,
            wsMsg
        );
    }
}