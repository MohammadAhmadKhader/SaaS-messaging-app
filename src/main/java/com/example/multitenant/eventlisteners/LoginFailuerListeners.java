package com.example.multitenant.eventlisteners;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.multitenant.services.cache.LoginAttemptsCacheService;
import com.example.multitenant.services.users.UsersService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginFailuerListeners {
    private final LoginAttemptsCacheService loginAttemptsCacheService;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        var username = (String) event.getAuthentication().getPrincipal();
        this.loginAttemptsCacheService.handleLoginFailed(username);
    }

    @EventListener
    public void onAuthenticationSucceeded(AuthenticationSuccessEvent event) {
        var principal = event.getAuthentication().getPrincipal();
        var username = "";
        if (principal instanceof String str) {
            username = str;
        } else if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        }
    
        this.loginAttemptsCacheService.handleLoginSucceeded(username);
    }
}