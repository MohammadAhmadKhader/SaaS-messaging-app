package com.example.multitenant.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.models.binders.MembershipKey;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.utils.AppUtils;

@Component
public class CustomSPEL {
    @Autowired
    private MemberShipService memberShipService;

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

        var membership = this.memberShipService.findUserMembershipWithRolesAndPermissions(tenantId, userId);
        if(membership == null) {
            return false;
        }
        
        var perms = membership.getOrganizationRoles().stream().flatMap((role) -> {
            return role.getOrganizationPermissions().stream().map((perm) -> {
                return perm.getName();
            });
        }).toList();

        return perms.stream().anyMatch((perm) -> perm.equals(permission));
    }

    public boolean hasOrgRole(String roleName) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal userDetails)) {
            return false;
        }

        var tenantId = AppUtils.getTenantId();
        var userId = userDetails.getUser().getId();

        var membership = this.memberShipService.findUserMembershipWithRolesAndPermissions(tenantId, userId);
        if(membership == null) {
            return false;
        }
        
        var roles = membership.getOrganizationRoles().stream().map((role) -> {
            return role.getName();
        }).toList();

        return roles.stream().anyMatch((role) -> role.equals(roleName));
    }
}