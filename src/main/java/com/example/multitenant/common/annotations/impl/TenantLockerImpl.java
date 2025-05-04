package com.example.multitenant.common.annotations.impl;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.example.multitenant.common.annotations.contract.TenantHandlerLocker;
import com.example.multitenant.exceptions.DistributedLockException;
import com.example.multitenant.services.disributedlock.DistributedLockService;
import com.example.multitenant.utils.AppUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This can be used in case you need to lock a specific handler for a tenant.
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class TenantLockerImpl {
    private final DistributedLockService distributedLockService;

    @Around("execution(* com.example.multitenant.controllers..*(..)) && @annotation(tenantLocker)")
    public Object lockTenant(ProceedingJoinPoint joinPoint, TenantHandlerLocker tenantLocker) throws Throwable {
        var orgId = AppUtils.getTenantId();
        var handlerName = joinPoint.getSignature().getName();
        var lockKey = this.distributedLockService.getTenantKey(orgId, handlerName.toLowerCase());

        var locked = this.distributedLockService.acquireLock(orgId, lockKey, 30, TimeUnit.SECONDS);
        if (!locked) {
            throw new DistributedLockException();
        }

        try {
            log.info("teant handler lock was acquired on organization with id {}", orgId);

            var result = joinPoint.proceed();
            return result;
        } finally {
            this.distributedLockService.releaseLock(orgId, lockKey);
            log.info("teant handler lock was released on organization with id {}", orgId);
        }
    }

}