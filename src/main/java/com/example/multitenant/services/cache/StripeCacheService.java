package com.example.multitenant.services.cache;

import java.io.Serializable;
import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.multitenant.common.annotations.contract.LogMethod;
import com.example.multitenant.dtos.categories.CategoryViewDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StripeCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    @LogMethod
    public Boolean hasUserRegistedInStripe(Long userId) {
        var key = this.getStripeUserKey(userId);
        var cached = this.redisTemplate.opsForValue().get(key);
        if(cached == null) {
            return false;
        }

        return (Boolean) cached;
    }

    public void setHasUserRegistered(Long userId, Boolean value) {
        var key = this.getStripeUserKey(userId);
        this.redisTemplate.opsForValue().set(key, value);
    }

    private String getStripeUserKey(Serializable userId) {
        return "stripe:user:" + userId;
    }
}
