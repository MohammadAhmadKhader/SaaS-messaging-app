package com.example.multitenant.common.annotations.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.multitenant.common.annotations.contract.*;
import com.example.multitenant.exceptions.DistributedLockException;
import com.example.multitenant.models.enums.*;
import com.example.multitenant.services.cache.*;
import com.example.multitenant.services.disributedlock.DistributedLockService;
import com.example.multitenant.utils.AppUtils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class SubscriptionLimitImpl {
    private final SubscriptionLimitChecker subscriptionLimitChecker;
    private final StripeSubsecriptionsCacheService stripeSubsecriptionsCacheService;
    private final DistributedLockService distributedLockService;

    @Around("execution(* com.example.multitenant.controllers..*(..)) && @annotation(validateLimit)")
    public Object validateGeneralLimit(ProceedingJoinPoint joinPoint, ValidateSubscriptionLimit validateLimit) throws Throwable {
        log.info("validation limit {} {}", validateLimit.toString(), validateLimit.limit());
        var orgId = AppUtils.getTenantId();
        var lockKey = this.distributedLockService.getKey(orgId, validateLimit.limit().toString());

        var locked = this.distributedLockService.acquireLock(orgId, lockKey, 30, TimeUnit.SECONDS);
        if (!locked) {
            throw new DistributedLockException();
        }
    
        try {
            log.info("validating general limit {} for org {}", validateLimit.limit(), orgId);
            var plan = getPlan(orgId);
            this.subscriptionLimitChecker.validateLimitNotExceeded(orgId, plan, validateLimit.limit());
        
            var result = joinPoint.proceed();
            this.handleGeneralOperation(orgId, validateLimit.limit(), validateLimit.counterOperation());
            return result;
        } finally {
            this.distributedLockService.releaseLock(orgId, lockKey);
            log.info("teant lock was released on organization with id {} on method: {}", orgId, "validateGeneralLimit");
        }
    }

    @Around("execution(* com.example.multitenant.controllers..*(..)) && @annotation(validateLimit)")
    public Object validateCategoryLimit(ProceedingJoinPoint joinPoint, ValidateCategoryChannelsSubscriptionLimit validateLimit) throws Throwable {
        var orgId = AppUtils.getTenantId();
        var args = joinPoint.getArgs();
        var categoryId = (Integer) args[validateLimit.categoryIdParamIndex()];
        var lockKey = this.distributedLockService.getKey(orgId, validateLimit.limit().toString());

        var locked = this.distributedLockService.acquireLock(orgId, lockKey, 30, TimeUnit.SECONDS);
        if (!locked) {
            throw new DistributedLockException();
        }

        try {
            log.info("validating category limit for org {} and category {}", orgId, categoryId);
            var plan = getPlan(orgId);
            this.subscriptionLimitChecker.validateCategoriesChannelsLimitNotExceeded(orgId, plan, validateLimit.limit(), categoryId);

            var result = joinPoint.proceed();
            this.handleCategoryOperation(orgId, categoryId, validateLimit.limit(), validateLimit.counterOperation());
            return result;
        } finally {
            this.distributedLockService.releaseLock(orgId, lockKey);
            log.info("teant lock was released on organization with id {} on method: {}", orgId, "validateCategoryLimit");
        }
    }

    private String getPlan(Integer orgId) {
        var sub = stripeSubsecriptionsCacheService.getSubscription(orgId);
        return (sub != null) ? sub.getTier() : StripePlan.FREE.toString();
    }

    private void handleGeneralOperation(Integer orgId, StripeLimit limit, StripeCounterOperation op) {
        switch (limit) {
            case MEMBERS -> {
                log.info("limit operation {}", StripeLimit.MEMBERS);
                Function<Integer, Long> incrementFn = (id) -> subscriptionLimitChecker.incrementOrgMembersCount(id);
                Function<Integer, Long> decrementFn = (id) -> subscriptionLimitChecker.decrementOrgMembersCount(id);

                handleRedisCounter(op, orgId, incrementFn, decrementFn);
            }

            case ROLES -> {
                log.info("limit operation {}", StripeLimit.ROLES);
                Function<Integer, Long> incrementFn = (id) -> subscriptionLimitChecker.incrementOrgRolesCount(id);
                Function<Integer, Long> decrementFn = (id) -> subscriptionLimitChecker.decrementOrgRolesCount(id);

                handleRedisCounter(op, orgId, incrementFn, decrementFn);
            }

            case CATEGORIES -> {
                log.info("limit operation {}", StripeLimit.CATEGORIES);
                Function<Integer, Long> incrementFn = (id) -> subscriptionLimitChecker.incrementOrgCategoriesCount(id);
                Function<Integer, Long> decrementFn = (id) -> subscriptionLimitChecker.decrementOrgCategoriesCount(id);

                handleRedisCounter(op, orgId, incrementFn, decrementFn);
            }

            default -> {
                var errMsg = "invalid StripeLimit received: " +limit;
                log.error(errMsg);
                throw new IllegalStateException(errMsg);
            }
        }
    }

    private void handleCategoryOperation(Integer orgId, Integer categoryId, StripeLimit limit, StripeCounterOperation op) {
        if (limit == StripeLimit.CATEGORY_CHANNELS) {
            Function<Integer, Long> incrementFn = (id) -> subscriptionLimitChecker.incrementCategoryChannelsCount(id, categoryId);
            Function<Integer, Long> decrementFn = (id) -> subscriptionLimitChecker.decrementCategoryChannelsCount(id, categoryId);

            handleRedisCounter(op, categoryId, incrementFn, decrementFn);
        }
    }

    private void handleRedisCounter(StripeCounterOperation op, Integer id, 
    Function<Integer, Long> incrementFn, Function<Integer, Long> decrementFn) {
        
        if (op == StripeCounterOperation.INCREMENT) {
            incrementFn.apply(id);

        } else if (op == StripeCounterOperation.DECREMENT) {
            decrementFn.apply(id);
        }
    }
}