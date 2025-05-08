package com.example.multitenant.services.cache;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.multitenant.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginAttemptsCacheService {
    public static Integer maxAttemptsAllowed = 5;
    private static Duration CACHE_TTL = Duration.ofMinutes(15);

    private final RedisTemplate<String, Long> redisTemplate;
    private final BlackListedIpsCacheService blackListedIpsCacheService;

    public void handleLoginFailed(String username) {
        var counter = this.incrementCounter(username);
        if(counter >= BlackListedIpsCacheService.maxLoginLockedTimesUntilBlackListed) {
            this.blackListedIpsCacheService.handleLoginSuspeciousIps();
        } 
    }

    public void handleLoginSucceeded(String username) {
        this.invalidateCounter(username);
    }

    public boolean isLocked(String username) {
        var key = this.getAttemptsCounterKey(username);
        var counterVal = this.redisTemplate.opsForValue().get(key);
        if(counterVal == null) {
            return false;
        }

        return counterVal >= maxAttemptsAllowed;
    }

    private Long incrementCounter(String username) {
        var key = this.getAttemptsCounterKey(username);
        var attempts = this.redisTemplate.opsForValue().increment(key);
        if(attempts != null && attempts.equals(1L)) {
            this.redisTemplate.expire(key, CACHE_TTL);
        }

        return attempts;
    }

    private void invalidateCounter(String username) {
        this.redisTemplate.delete(this.getAttemptsCounterKey(username));
    }

    private String getAttemptsCounterKey(String username) {
        return "login:attempts:" + username;
    }
}