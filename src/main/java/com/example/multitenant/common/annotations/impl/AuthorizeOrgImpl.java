package com.example.multitenant.common.annotations.impl;



import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.multitenant.common.annotations.contract.AuthorizeOrg;
import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.exceptions.UnauthorizedUserException;

import com.example.multitenant.services.security.OrganizationPermissionsService;

@Aspect
@Component
public class AuthorizeOrgImpl {
    @Autowired
    OrganizationPermissionsService organizationPermissionsService;

    @Before("annotation(com.example.multitenant.annotations.contract.AuthorizeOrg)")
    public void checkOrgPermissions(JoinPoint joinPoint) {
        var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        var annotation = method.getAnnotation(AuthorizeOrg.class);

        var allowedPerms = annotation.value();

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userDetails)) {
            throw new UnauthorizedUserException("Unauthorized");
        }

        var requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            throw new UnauthorizedUserException("Unauthorized");
        }

        var req = servletRequestAttributes.getRequest();
        var tenantId = req.getHeader("X-Tenant-ID");
        var tenantIdAsInt = Integer.parseInt(tenantId);

        var userId = userDetails.getUser().getId();
        var hasPermission = this.organizationPermissionsService.hasPermission(userId, tenantIdAsInt, allowedPerms);
        if(!hasPermission) {
            throw new UnauthorizedUserException("Unauthorized");
        }
    }
}
