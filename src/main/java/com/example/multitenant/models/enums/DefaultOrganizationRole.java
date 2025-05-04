package com.example.multitenant.models.enums;

import lombok.Getter;

@Getter
public enum DefaultOrganizationRole {
    ORG_USER("Org-User", "User"),
    ORG_ADMIN("Org-Admin", "Admin"),
    ORG_OWNER("Org-Owner", "Owner");

    private final String roleName;
    private final String defaultDisplayName;

    public static boolean isDefaultRole(String name) {
        for (var role : DefaultOrganizationRole.values()) {
            if (role.getRoleName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    DefaultOrganizationRole(String roleName, String defaultDisplayName) {
        this.roleName = roleName;
        this.defaultDisplayName = defaultDisplayName;
    }
}
