package com.example.multitenant.utils;

import java.beans.FeatureDescriptor;
import java.security.Principal;
import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.*;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.Conversation;
import com.example.multitenant.models.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

public class AppUtils {
    public static Integer getTenantId() {
        @SuppressWarnings("null")
        var req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        var tenantIdStr = req.getHeader("X-Tenant-ID");
        
        return Integer.parseInt(tenantIdStr);
    }

    public static User getWsTarget(User sender, Conversation conv) {
        if(conv == null) {
            throw new IllegalStateException("conversation was received as null");
        }

        if(conv.getUser1() == null) {
            throw new IllegalStateException("user1 was received as null");
        }

        if(conv.getUser2() == null) {
            throw new IllegalStateException("user2 was received as null");
        }

        if(sender == null) {
            throw new IllegalStateException("sender was received as null");
        }

        if(conv.getUser1().getId() != sender.getId()) {
            return conv.getUser1();
        }
        
        return conv.getUser2();
    }

    public static String getClientIp(HttpServletRequest request) {
        var ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0];
        }

        return ip;
    }

    public static String getUserAgrent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}