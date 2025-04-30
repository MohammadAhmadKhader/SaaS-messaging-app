package com.example.multitenant.services.security;

import org.springframework.stereotype.Component;

@Component
public class OrganizationPermissions {
    public static final String ROLE_VIEW = "org-dashboard:role:view";
    public static final String ROLE_CREATE = "org-dashboard:role:create";
    public static final String ROLE_UPDATE = "org-dashboard:role:update";
    public static final String ROLE_DELETE = "org-dashboard:role:delete";
    public static final String ROLE_ASSIGN = "org-dashboard:role:assign";
    public static final String ROLE_UN_ASSIGN = "org-dashboard:role:un-assign";

    public static final String PERMISSION_ASSIGN = "org-dashboard:permission:assign";
    public static final String PERMISSION_UN_ASSIGN = "org-dashboard:permission:un-assign";
    
    public static final String DASH_ORGANIZATION_UPDATE = "org-dashboard:organization:update"; // new

    public static final String USER_INVITE = "organization:user:invite";
    public static final String USER_KICK = "organization:user:kick";

    public static final String TRANSFER_OWNERSHIP = "organization:transfer-ownership";

    public static final String CATEGORY_VIEW = "organization:category:view";
    public static final String CATEGORY_CREATE = "organization:category:create";
    public static final String CATEGORY_UPDATE = "organization:category:update";
    public static final String CATEGORY_DELETE = "organization:category:delete";
    
    public static final String CHANNEL_CREATE = "organization:channel:create";
    public static final String CHANNEL_UPDATE = "organization:channel:update";
    public static final String CHANNEL_DELETE = "organization:channel:delete";
}
