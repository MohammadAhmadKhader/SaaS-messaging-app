package com.example.multitenant.models.enums;

import lombok.Getter;

@Getter
public enum DefaultOrganizationRole {
    ORG_USER("Org-User"),
    ORG_ADMIN("Org-Admin"),
    ORG_OWNER("Org-Owner");

    private final String roleName;

    DefaultOrganizationRole(String roleName) {
        this.roleName = roleName;
    }
}
