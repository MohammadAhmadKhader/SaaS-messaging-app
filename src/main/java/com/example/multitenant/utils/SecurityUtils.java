package com.example.multitenant.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;

@Component
public class SecurityUtils {
    public static UserPrincipal getPrincipal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = ((UserPrincipal) auth.getPrincipal());

        return user;
    }
}
