package com.example.multitenant.models.enums;

import lombok.Getter;

@Getter
public enum DefaultOrganizationRole {
    ORG_USER("Org-User"),
    ORG_ADMIN("Org-Admin"),
    ORG_OWNER("Org-Owner");

    private final String roleName;

    public static boolean isDefaultRole(String roleName) {
        if (
            DefaultOrganizationRole.ORG_OWNER.getRoleName().equals(roleName) ||
            DefaultOrganizationRole.ORG_ADMIN.getRoleName().equals(roleName) || 
            DefaultOrganizationRole.ORG_USER.getRoleName().equals(roleName)
        ) {

            return true;
        }

        return false;
    }

    DefaultOrganizationRole(String roleName) {
        this.roleName = roleName;
    }
}
