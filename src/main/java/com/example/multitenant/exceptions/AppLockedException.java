package com.example.multitenant.exceptions;

import org.springframework.security.authentication.LockedException;

import lombok.Getter;

@Getter
public class AppLockedException extends LockedException {
    private static final String defaultMessage = 
    "your account has been temporarily locked due to multiple invalid login attempts. please try again after 15 minutes";
    private String email;
    public AppLockedException(String email) {
        super(defaultMessage);
        this.email = email;
    }
}
