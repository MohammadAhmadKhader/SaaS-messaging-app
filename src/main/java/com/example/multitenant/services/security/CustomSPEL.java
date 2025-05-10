package com.example.multitenant.services.security;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.models.User;
import com.example.multitenant.services.cache.*;
import com.example.multitenant.utils.AppUtils;
import com.example.multitenant.utils.SecurityUtils;

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
        var user = SecurityUtils.getUserFromAuth();
        if (user == null) {
            return false;
        }

        var tenantId = AppUtils.getTenantId();
        var userId = user.getId();

        var perms = this.authCacheService.getUserOrgPermissions(tenantId, userId);

        return perms.stream().anyMatch((perm) -> perm.equals(permission));
    }

    public boolean hasOrgRole(String roleName) {
        var user = SecurityUtils.getUserFromAuth();
        if (user == null) {
            return false;
        }

        var tenantId = AppUtils.getTenantId();
        var userId = user.getId();

        var roles = this.authCacheService.getUserOrgRoles(tenantId, userId);

        return roles.stream().anyMatch((role) -> role.getName().equals(roleName));
    }

    public boolean hasCategoryAccess(Integer categoryId) {
        var user = SecurityUtils.getUserFromAuth();
        if (user == null) {
            return false;
        }

        var tenantId = AppUtils.getTenantId();
        var userId = user.getId();

        var userRolesIds = this.authCacheService.getUserOrgRoles(tenantId, userId)
                                            .stream()
                                            .map((role) -> role.getId())
                                            .toList();

        var authoriedRolesIds = this.authCacheService
                                    .getOrgCategoryWithAuthorizedRolesList(tenantId, categoryId);

        return authoriedRolesIds.stream().anyMatch((roleId)-> userRolesIds.contains(roleId));
    }
}