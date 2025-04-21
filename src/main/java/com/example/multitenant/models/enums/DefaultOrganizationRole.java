package com.example.multitenant.models.enums;

import lombok.Getter;

@Getter
public enum DefaultOrganizationRole {
    ORG_USER("Org-User"),
    ORG_ADMIN("Org-Admin"),
    ORG_OWNER("Org-Owner");

    private final String roleName;

    public static boolean isDefaultRole(String name) {
        for (var role : DefaultOrganizationRole.values()) {
            if (role.getRoleName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    DefaultOrganizationRole(String roleName) {
        this.roleName = roleName;
    }
}
