package com.example.multitenant.services.cache;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.multitenant.services.organizationsrestrictions.OrganizationRestrictionsService;
import com.example.multitenant.services.restrictions.RestrictionsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrgRestrictionsCacheSerivce {
    private final RedisTemplate<String, Boolean> redisTemplate;
    private final OrganizationRestrictionsService organizationRestrictionsService;
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    public Boolean getIsRestricted(Integer orgId, Long userId) {
        var val = this.getKeyValue(orgId, userId);
        if(val == null) {
            var isRestricted = this.fetchRestriction(orgId, userId);
            this.setKey(orgId, userId, isRestricted);
        } 
        
        return val;
    }

    public void setKey(Integer orgId, Long userId ,Boolean value) {
        var key = this.getKey(orgId, userId);
        this.redisTemplate.opsForValue().set(key, value, CACHE_TTL);
    }

    public void invalidateKey(Integer orgId, Long userId) {
        this.redisTemplate.delete(this.getKey(orgId, userId));
    }

    private boolean fetchRestriction(Integer orgId, Long userId) {
        return this.organizationRestrictionsService.isUserRestricted(userId, orgId);
    }

    private Boolean getKeyValue(Integer orgId, Long userId) {
        var key = this.getKey(orgId, userId);
        return this.redisTemplate.opsForValue().get(key);
    }

    private String getKey(Integer orgId, Long userId) {
        return "org:"+ orgId +":user:"+ userId +":restrictions" ;
    }
}