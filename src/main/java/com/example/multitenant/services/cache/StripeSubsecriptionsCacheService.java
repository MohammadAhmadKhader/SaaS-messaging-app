package com.example.multitenant.services.cache;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.multitenant.models.InternalStripeSubscription;
import com.example.multitenant.services.stripe.StripeService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StripeSubsecriptionsCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StripeService stripeService;
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    public InternalStripeSubscription getSubscription(Integer orgId) {
        var key = this.getKey(orgId);
        var cached = redisTemplate.opsForValue().get(key);
        if (cached == null) {
            return loadSubscription(orgId);
        }

        return (InternalStripeSubscription) cached;
    }

    public InternalStripeSubscription loadSubscription(Integer orgId) {
        var subscription = this.stripeService.getOrgActiveSubsecription(orgId);
        if (subscription != null) {
            redisTemplate.opsForValue().set(getKey(orgId), subscription, CACHE_TTL);
        }

        return subscription;
    }

    public void setSubscription(Integer orgId, InternalStripeSubscription subscription) {
        var key = getKey(orgId);
        redisTemplate.opsForValue().set(key, subscription, CACHE_TTL);
    }

    public void invalidateSubscriptionCache(Integer orgId) {
        redisTemplate.delete(getKey(orgId));
    }

    private String getKey(Integer orgId) {
        return "org:subscription:" + orgId;
    }
}