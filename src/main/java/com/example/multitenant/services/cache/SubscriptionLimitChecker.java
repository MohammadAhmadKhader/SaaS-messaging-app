package com.example.multitenant.services.cache;

import java.io.Serializable;
import java.time.Duration;
import java.util.function.Function;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.multitenant.common.annotations.contract.LogMethod;
import com.example.multitenant.config.StripePlansConfig;
import com.example.multitenant.exceptions.PlanLimitExceededException;
import com.example.multitenant.models.enums.StripeLimit;
import com.example.multitenant.models.enums.StripePlan;
import com.example.multitenant.services.categories.CategoriesService;
import com.example.multitenant.services.channels.ChannelsService;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.security.OrganizationRolesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionLimitChecker {
    private final MemberShipService memberShipService;
    private final OrganizationRolesService organizationRolesService;
    private final CategoriesService categoriesService;
    private final ChannelsService channelsService;
    private final RedisTemplate<String, Long> redisTemplate;
    private final StripePlansConfig stripePlansConfig;

    public void validateLimitNotExceeded(Integer orgId, String plan, StripeLimit limit) {
        var currentCount = this.getCounterFunction(limit).apply(orgId);
        var maxAllowed = this.stripePlansConfig.getMaxAllowed(limit, StripePlan.fromValue(plan));
    
        if (currentCount >= maxAllowed) {
            var errMsg = String.format("%s exceeded limit for plan: '%s'", limit.getLimit(), plan);
            log.warn(errMsg);
            throw new PlanLimitExceededException(errMsg);
        }
    }

    public void validateCategoriesChannelsLimitNotExceeded(Integer orgId, String plan, StripeLimit limit, Integer categoryId) {
        var currentCount = this.getOrgCategoryChannelCount(orgId, categoryId);
        var maxAllowed = this.stripePlansConfig.getMaxAllowed(limit, StripePlan.fromValue(plan));
    
        if (currentCount >= maxAllowed) {
            var errMsg = String.format("%s exceeded for plan: '%s'", limit.getLimit(), plan);
            log.warn(errMsg);
            throw new PlanLimitExceededException(errMsg);
        }
    }

    @LogMethod
    public long getOrgRolesCount(Integer orgId) {
        var key = this.getOrgRolesCountKey(orgId);
        var cached = this.redisTemplate.opsForValue().get(key);
        if(cached == null) {
            return this.loadOrgRolesCount(orgId);
        }

        return (long) cached;
    }

    @LogMethod
    public long getOrgMembersCount(Integer orgId) {
        var key = this.getOrgMembersCountKey(orgId);
        var cached = this.redisTemplate.opsForValue().get(key);
        if(cached == null) {
            return this.loadOrgMembersCount(orgId);
        }

        return (long) cached;
    }

    @LogMethod
    public long getOrgCategoriesCount(Integer orgId) {
        var key = this.getOrgCategoriesCountKey(orgId);
        var cached = this.redisTemplate.opsForValue().get(key);
        if(cached == null) {
            return this.loadOrgCategoriesCount(orgId);
        }

        return cached;
    }

    @LogMethod
    public long getOrgCategoryChannelCount(Integer orgId, Integer categoryId) {
        var key = this.getCategoryChannelsCountKey(orgId, categoryId);
        var cached = this.redisTemplate.opsForValue().get(key);
        if(cached == null) {
            return this.loadCategoryChannelsCount(orgId, categoryId);
        }

        return (long) cached;
    }

    // * org category channels functions
    public long loadCategoryChannelsCount(Integer orgId, Integer categoryId) {
        var categoryChannelsCount = this.channelsService.countChannelsByCategoryId(orgId, categoryId);
        var key = this.getCategoryChannelsCountKey(orgId, categoryId);
        this.redisTemplate.opsForValue().set(key, categoryChannelsCount);
        return categoryChannelsCount;
    }

    public Long incrementCategoryChannelsCount(Integer orgId, Integer categoryId) {
        var key = this.getCategoryChannelsCountKey(orgId, categoryId);
        if(this.redisTemplate.hasKey(key)) {
            return this.redisTemplate.opsForValue().increment(key);
        } 

        return this.loadCategoryChannelsCount(orgId, categoryId);
    }

    public Long decrementCategoryChannelsCount(Integer orgId, Integer categoryId) {
        var key = this.getCategoryChannelsCountKey(orgId, categoryId);
        if(this.redisTemplate.hasKey(key)) {
            return this.redisTemplate.opsForValue().decrement(key);
        } 

        return this.loadCategoryChannelsCount(orgId, categoryId);
    }

    public void setCategoryChannelsCount(Integer orgId, Integer categoryId, long categoriesCount) {
        var key = this.getCategoryChannelsCountKey(orgId, categoryId);
        this.redisTemplate.opsForValue().set(key, categoriesCount);
    }

    private String getCategoryChannelsCountKey(Serializable orgId, Serializable categoryId) {
        return "org:limit-checker:" + orgId +":category:"+ categoryId +":channels-count";
    }

    // * org roles functions
    public long loadOrgRolesCount(Integer orgId) {
        var channelCategoryCount = this.organizationRolesService.countOrganizationRoles(orgId);
        var key = this.getOrgRolesCountKey(orgId);
        this.redisTemplate.opsForValue().set(key, channelCategoryCount);
        return channelCategoryCount;
    }
    
    public Long incrementOrgRolesCount(Integer orgId) {
        var key = this.getOrgRolesCountKey(orgId);
        if(this.redisTemplate.hasKey(key)) { 
            return this.redisTemplate.opsForValue().increment(key);
        }
        
        return this.loadOrgRolesCount(orgId);
    }

    public Long decrementOrgRolesCount(Integer orgId) {
        var key = this.getOrgRolesCountKey(orgId);
        if(this.redisTemplate.hasKey(key)) {
            return this.redisTemplate.opsForValue().decrement(key);
        }

        return this.loadOrgRolesCount(orgId);
    }
    
    public void setOrgRolesCount(Integer orgId, long rolesCount) {
        var key = this.getOrgRolesCountKey(orgId);
        this.redisTemplate.opsForValue().set(key, rolesCount);
    }

    private String getOrgRolesCountKey(Serializable orgId) {
        return "org:limit-checker:" + orgId +":roles-count";
    }

    // * org categories functions
    public long loadOrgCategoriesCount(Integer orgId) {
        var categoriesCount = this.categoriesService.countOrganizationCategories(orgId);
        var key = this.getOrgCategoriesCountKey(orgId);
        this.redisTemplate.opsForValue().set(key, categoriesCount);
        return categoriesCount;
    }

    public Long incrementOrgCategoriesCount(Integer orgId) {
        var key = this.getOrgCategoriesCountKey(orgId);
        if(this.redisTemplate.hasKey(key)) {
            return this.redisTemplate.opsForValue().increment(key); 
        }

        return this.loadOrgCategoriesCount(orgId);
    }

    public Long decrementOrgCategoriesCount(Integer orgId) {
        var key = this.getOrgCategoriesCountKey(orgId);
        if(this.redisTemplate.hasKey(key)) { 
            return this.redisTemplate.opsForValue().decrement(key);
        }

        return this.loadOrgCategoriesCount(orgId);
    }

    public void setOrgCategoriesCount(Integer orgId, long categoriesCount) {
        var key = this.getOrgCategoriesCountKey(orgId);
        this.redisTemplate.opsForValue().set(key, categoriesCount);
    }

    private String getOrgCategoriesCountKey(Serializable orgId) {
        return "org:limit-checker:" + orgId +":categories-count";
    }

    // * org members functions
    public long loadOrgMembersCount(Integer orgId) {
        var membersCount = this.memberShipService.countOrganizationMembers(orgId);
        var key = this.getOrgMembersCountKey(orgId);
        this.redisTemplate.opsForValue().set(key, membersCount);
        return membersCount;
    }

    public Long incrementOrgMembersCount(Integer orgId) {
        var key = this.getOrgMembersCountKey(orgId);
        if(this.redisTemplate.hasKey(key)) {
            return this.redisTemplate.opsForValue().increment(key);
        }

        return loadOrgMembersCount(orgId);
    }

    public Long decrementOrgMembersCount(Integer orgId) {
        var key = this.getOrgMembersCountKey(orgId);
        if(this.redisTemplate.hasKey(key)) { 
            return  this.redisTemplate.opsForValue().decrement(key);
        }

        return loadOrgMembersCount(orgId);
    }

    public void setOrgMembersCount(Integer orgId, long membersCount) {
        var key = this.getOrgMembersCountKey(orgId);
        this.redisTemplate.opsForValue().set(key, membersCount);
    }

    private String getOrgMembersCountKey(Serializable orgId) {
        return "org:limit-checker:" + orgId +":members-count";
    }

    private Function<Integer, Long> getCounterFunction(StripeLimit limit) {
        switch (limit) {
            case MEMBERS:
                return this::getOrgMembersCount;
            case ROLES:
                return this::getOrgRolesCount;
            case CATEGORIES:
                return this::getOrgCategoriesCount;
            default:
                log.error("invalid limit was received {}", limit);
                throw new IllegalArgumentException("unsupported StripeLimit: " + limit);
        }
    }
}