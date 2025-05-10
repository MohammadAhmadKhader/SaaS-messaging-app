package com.example.multitenant.services.cache;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.multitenant.services.restrictions.RestrictionsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RestrictionsCacheSerivce {
    private final RedisTemplate<String, Boolean> redisTemplate;
    private final RestrictionsService restrictionsService;
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);
    
    public Boolean getIsRestricted(Long userId) {
        var val = this.getKeyValue(userId);
        if(val == null) {
            val = this.fetchRestriction(userId);
            this.setKey(userId, val);
        } 
        
        return val;
    }

    public void setKey(Long userId ,Boolean value) {
        var key = this.getKey(userId);
        this.redisTemplate.opsForValue().set(key, value, CACHE_TTL);
    }

    public void invalidateKey(Long userId) {
        this.redisTemplate.delete(this.getKey(userId));
    }

    private boolean fetchRestriction(Long userId) {
        return this.restrictionsService.isUserRestricted(userId);
    }

    private Boolean getKeyValue(Long userId) {
        var key = this.getKey(userId);
        return this.redisTemplate.opsForValue().get(key);
    }

    private String getKey(Long userId) {
        return "app:restrictions:" + userId;
    }
}