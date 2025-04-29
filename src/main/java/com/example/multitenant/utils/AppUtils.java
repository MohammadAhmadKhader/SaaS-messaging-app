package com.example.multitenant.utils;

import java.beans.FeatureDescriptor;
import java.security.Principal;
import java.util.Arrays;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.Conversation;
import com.example.multitenant.models.User;
import com.example.multitenant.services.cache.RedisService;

import lombok.RequiredArgsConstructor;

public class AppUtils {
    public static <TModel> void copyNonNullProperties(TModel source, TModel target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    // TODO: refactor this function
    private static String[] getNullPropertyNames(Object source) {
        var src = new BeanWrapperImpl(source);

        return Arrays.stream(src.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    public static Integer getTenantId() {
        var req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        var tenantIdStr = req.getHeader("X-Tenant-ID");
        
        return Integer.parseInt(tenantIdStr);
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
}
