package com.example.multitenant.models.enums;

import lombok.Getter;

@Getter
public enum DefaultGlobalRole {
    SUPERADMIN("SuperAdmin"),
    ADMIN("Admin"),
    USER("User");

    private final String roleName;

    public static boolean isDefaultRole(String name) {
        for (var role : DefaultGlobalRole.values()) {
            if (role.getRoleName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    DefaultGlobalRole(String roleName) {
        this.roleName = roleName;
    }
}