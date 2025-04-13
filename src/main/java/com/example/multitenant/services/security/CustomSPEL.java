package com.example.multitenant.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.models.binders.MembershipKey;
import com.example.multitenant.services.membership.MemberShipService;

@Component
public class CustomSPEL {
    @Autowired
    private MemberShipService memberShipService;

    public boolean hasAnyRole(Authentication auth) {
        return auth.getAuthorities().stream()
            .anyMatch(granted -> granted.getAuthority().startsWith("ROLE_"));
    }

    // public boolean hasOrgAuthority(Authentication auth, String tenantId) {
    //     var authentication = SecurityContextHolder.getContext().getAuthentication();
    //     if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userDetails)) {
    //         return false;
    //     }

    //     var tenantIdAsInt = Integer.parseInt(tenantId);
    //     var userId = userDetails.getUser().getId();

    //     var membership = this.memberShipService.findById(new MembershipKey(tenantIdAsInt, userId));
    //     membership.getOrganizationRoles().stream().flatMap((role) -> {
    //         var perms = role.getOrganizationPermissions();
          
    //         return perms.stream();
    //     }).anyMatch((perm) -> permsList.contains(perm.getName()));

    //     return true;
    // }
}