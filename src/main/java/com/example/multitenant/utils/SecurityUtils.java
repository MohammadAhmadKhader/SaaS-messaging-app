package com.example.multitenant.utils;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.User;

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

    public static Long getUserIdFromAuth() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof Authentication authentication) {
                var principalObj = authentication.getPrincipal();
                if (principalObj instanceof UserPrincipal userPrincipal) {
                    return userPrincipal.getUser().getId();
                }
            }

            throw new UnknownException("unable to extract user id from auth");
        } catch (Exception e) {
            throw new UnknownException("an error has occured durign attempt to fetch user from auth");
        }
    }

    public static User getUserFromAuth() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof Authentication authentication) {
                var principalObj = authentication.getPrincipal();
                if (principalObj instanceof UserPrincipal userPrincipal) {
                    return userPrincipal.getUser();
                }
            }

            throw new UnknownException("unable to extract user from auth");
        } catch (Exception e) {
            throw new UnknownException("an error has occured durign attempt to fetch user from auth");
        }
    }

    public static User getUserFromPrincipal(Principal principal) {
        try {
            var auth = (Authentication) principal;
            if (auth instanceof Authentication authentication) {
                var principalObj = authentication.getPrincipal();
                if (principalObj instanceof UserPrincipal userPrincipal) {
                    return userPrincipal.getUser();
                }
            }

            throw new UnknownException("unable to extract user from principal");
        } catch (Exception e) {
            throw new UnknownException("an error has occured durign attempt to fetch user from principal");
        }
    }
}
