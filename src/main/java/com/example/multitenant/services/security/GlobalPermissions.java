package com.example.multitenant.services.security;

import org.springframework.stereotype.Component;

@Component("globalPermissions")
public class GlobalPermissions {
    public static final String DASH_USER_VIEW = "app-dashboard:user:view";
    public static final String DASH_USER_CREATE = "app-dashboard:user:create";
    public static final String DASH_USER_DELETE = "app-dashboard:user:delete";
    public static final String DASH_ROLE_VIEW = "app-dashboard:role:view";
    public static final String DASH_ROLE_CREATE = "app-dashboard:role:create";
    public static final String DASH_ROLE_UPDATE = "app-dashboard:role:update";
    public static final String DASH_ROLE_ASSIGN = "app-dashboard:role:assign";
    public static final String DASH_ROLE_UN_ASSIGN = "app-dashboard:role:un-assign";
    public static final String DASH_PERMISSION_ASSIGN = "app-dashboard:permission:assign";
    public static final String DASH_PERMISSION_UN_ASSIGN = "app-dashboard:permission:un-assign";
    public static final String DASH_ROLE_DELETE = "app-dashboard:role:delete";
    public static final String DASH_ORGANIZATION_VIEW = "app-dashboard:organization:view";
    public static final String DASH_ORGANIZATION_CREATE = "app-dashboard:organization:create";
    public static final String DASH_ORGANIZATION_UPDATE = "app-dashboard:organization:update";
    public static final String DASH_ORGANIZATION_DELETE = "app-dashboard:organization:delete";
    public static final String DASH_ORGANIZATION_REMOVE_OWNERSHIP = "app-dashboard:organization:remove-ownership";
    public static final String DASH_ORGANIZATION_ASSIGN_OWNERSHIP = "app-dashboard:organization:assign-ownership";
    public static final String ORG_CREATE = "organization:create";
}