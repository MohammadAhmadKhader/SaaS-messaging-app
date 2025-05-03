package com.example.multitenant.eventlisteners;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.multitenant.utils.AppUtils;
import com.example.multitenant.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        var principal = event.getUser();
        var user = SecurityUtils.getUserFromPrincipal(principal);
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        var sessionId = accessor.getSessionId();

        if (user != null) {
            log.info("[Principal] principal name is  " + principal.getName());
            log.info("user '{}' connected (sessionId: {})", user.getFirstName(), sessionId);
        } else {
            log.warn("user was received as null wiht prinicpal: " + principal.getName());
        }
    }

    @EventListener
    public void handleWebScoketDisconnectListener(SessionDisconnectEvent event) { 
        var user = event.getUser();

        if(user != null) {
            log.info("user with name {} has disconnected", user.getName());

        } else {
            log.error("user was received as null");
        }
    }
}
