package com.example.multitenant.interceptors;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.example.multitenant.dtos.auth.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Deprecated
/**
 * unused so far does not change anything on the current implementation,
 * 1. we must have one that authorize (tenantId) with the (categoryId)
 * 2. another interceptor that handles the outgoing messages to ensure only the authorized user on the category will receive the events.
 *  */ 
public class SessionHandshakeInterceptor extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return null;
        }

        return request.getPrincipal();
    }

}
