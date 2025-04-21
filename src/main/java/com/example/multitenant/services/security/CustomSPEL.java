package com.example.multitenant.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.services.cache.SessionsService;
import com.example.multitenant.utils.AppUtils;

@Component
public class CustomSPEL {

    @Autowired
    private SessionsService sessionsService;

    public boolean hasAnyRole(Authentication auth) {
        return auth.getAuthorities().stream()
            .anyMatch(granted -> granted.getAuthority().startsWith("ROLE_"));
    }

    public boolean hasOrgAuthority(String permission) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal userDetails)) {
            return false;
        }

        var tenantId = AppUtils.getTenantId();
        var userId = userDetails.getUser().getId();

        var perms = this.sessionsService.getUserOrgPermissions(tenantId, userId);

        return perms.stream().anyMatch((perm) -> perm.equals(permission));
    }

    public boolean hasOrgRole(String roleName) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal userDetails)) {
            return false;
        }

        var tenantId = AppUtils.getTenantId();
        var userId = userDetails.getUser().getId();

        var roles = this.sessionsService.getUserOrgRoles(tenantId, userId);

        return roles.stream().anyMatch((role) -> role.equals(roleName));
    }
}