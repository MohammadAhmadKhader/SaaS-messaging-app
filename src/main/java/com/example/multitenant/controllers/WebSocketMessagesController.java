package com.example.multitenant.controllers;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.dtos.messages.MessageCreateDTO;
import com.example.multitenant.exceptions.UnauthorizedUserException;
import com.example.multitenant.models.User;
import com.example.multitenant.services.cache.RedisService;
import com.example.multitenant.services.messages.MessagesService;
import com.example.multitenant.services.users.UsersService;
import com.example.multitenant.services.websocket.WebSocketService;
import com.example.multitenant.utils.AppUtils;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class WebSocketMessagesController {
    private final MessagesService messagesService;
    private final WebSocketService webSocketService;

    @MessageMapping("/tenant/{tenantId}/channel/{channelId}/send")
    public void handleSendMessageToChannel(@Payload @Validated MessageCreateDTO payload,
        @DestinationVariable Integer tenantId, @DestinationVariable Integer channelId, Principal principal) {
            
        var user = AppUtils.getUserFromPrincipal(principal);
        if(user != null) {
            log.info("received message {}", payload.getContent());
            log.info("principal {}", user.getFirstName());

            var message = payload.toModel();
            message.setSender(user);
            message.setIsUpdated(false);

            var createdMsg = this.messagesService.create(message, channelId, tenantId);
            this.webSocketService.publishNewMessage(createdMsg, tenantId);
            
        } else {
            log.error("user was not found during attempt to fetch it from principal");
            throw new UnauthorizedUserException("unauthorized");
        }
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        log.warn("websocket error occurred: {}", exception.getMessage());
        return exception.getMessage();
    }
}
