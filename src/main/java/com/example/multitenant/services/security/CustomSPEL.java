package com.example.multitenant.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.services.cache.*;
import com.example.multitenant.utils.AppUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomSPEL {
    private final AuthCacheService authCacheService;

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

        var perms = this.authCacheService.getUserOrgPermissions(tenantId, userId);

        return perms.stream().anyMatch((perm) -> perm.equals(permission));
    }

    public boolean hasOrgRole(String roleName) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal userDetails)) {
            return false;
        }

        var tenantId = AppUtils.getTenantId();
        var userId = userDetails.getUser().getId();

        var roles = this.authCacheService.getUserOrgRoles(tenantId, userId);

        return roles.stream().anyMatch((role) -> role.getName().equals(roleName));
    }

    public boolean hasCategoryAccess(Integer categoryId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal userDetails)) {
            return false;
        }

        var tenantId = AppUtils.getTenantId();
        var userId = userDetails.getUser().getId();

        var userRolesIds = this.authCacheService.getUserOrgRoles(tenantId, userId)
                                            .stream()
                                            .map((role) -> role.getId())
                                            .toList();

        var authoriedRolesIds = this.authCacheService
                                    .getOrgCategoryWithAuthorizedRolesList(tenantId, categoryId);

        return authoriedRolesIds.stream().anyMatch((roleId)-> userRolesIds.contains(roleId));
    }
}