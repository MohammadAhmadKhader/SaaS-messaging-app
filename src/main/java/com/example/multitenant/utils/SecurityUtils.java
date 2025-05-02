package com.example.multitenant.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.exceptions.GlobalExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SecurityUtils {
    public static UserPrincipal getPrincipal() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                log.info("user unathorized");
                return null;
            }
            
            var principal = auth.getPrincipal();
            if (principal instanceof UserPrincipal) {
                return (UserPrincipal) principal;
            }
            
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
