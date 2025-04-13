package com.example.multitenant.models.enums;

import lombok.Getter;

@Getter
public enum DefaultGlobalRole {
    SUPERADMIN("SuperAdmin"),
    ADMIN("Admin"),
    USER("User");

    private final String roleName;

    DefaultGlobalRole(String roleName) {
        this.roleName = roleName;
    }
}