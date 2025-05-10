package com.example.multitenant.common.annotations.impl;


import java.security.Principal;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.multitenant.common.annotations.contract.CheckRestricted;
import com.example.multitenant.models.User;
import com.example.multitenant.services.cache.RestrictionsCacheSerivce;
import com.example.multitenant.utils.AppUtils;
import com.example.multitenant.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class CheckRestrictedImpl {
    private final RestrictionsCacheSerivce restrictionsCacheSerivce;

    // * combining both with '||' does not work and make 'checkRestricted' gets received as null on methods
    @Around("@annotation(checkRestricted)")
    public Object checkMethodAnnotation(ProceedingJoinPoint joinPoint, CheckRestricted checkRestricted) throws Throwable {
        return checkRestriction(joinPoint, checkRestricted);
    }
    
    @Around("@within(checkRestricted)")
    public Object checkClassAnnotation(ProceedingJoinPoint joinPoint, CheckRestricted checkRestricted) throws Throwable {
        return checkRestriction(joinPoint, checkRestricted);
    }
    
    private Object checkRestriction(ProceedingJoinPoint joinPoint, CheckRestricted checkRestricted) throws Throwable {
        User user;
        if (!checkRestricted.isWebsocket()) {
            user = SecurityUtils.getUserFromAuth();
        } else {
            var principal = this.extractPrincipalFromJoinPoint(joinPoint);
            user = SecurityUtils.getUserFromPrincipal(principal);
        }
        
        if (user == null) {
            throw new AccessDeniedException("user not authenticated");
        }
        
        var isRestricted = this.restrictionsCacheSerivce.getIsRestricted(user.getId());
        if (isRestricted) {
            throw new AccessDeniedException("access denied due to restriction");
        }
        
        return joinPoint.proceed();
    }

    private Principal extractPrincipalFromJoinPoint(ProceedingJoinPoint joinPoint) {
        var args = joinPoint.getArgs();
        for (var arg : args) {
            if (arg instanceof Principal) {
                return (Principal) arg;
            }
        }

        throw new IllegalArgumentException("you must pass 'Principal' in arguments");
    }
}
