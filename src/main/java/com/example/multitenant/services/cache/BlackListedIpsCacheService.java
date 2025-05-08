package com.example.multitenant.services.cache;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.multitenant.utils.AppUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlackListedIpsCacheService {
    public static Integer maxLoginLockedTimesUntilBlackListed = 5;
    public static String defaultLockedMessage = "your IP has been temporarily blacklisted due to suspicious activity.";
    private static Duration CACHE_TTL = Duration.ofHours(4);

    private final RedisTemplate<String, Long> redisTemplate;

    public void handleLoginSuspeciousIps() {
        var ip = AppUtils.getClientIp();
        this.incrementCounter(ip);
    }

    public boolean isBlackListedIp(HttpServletRequest request) {
        var ip = AppUtils.getClientIp(request);
        var key = this.getKey(ip);
        var count = this.redisTemplate.opsForValue().get(key);
        if(count == null) {
            return false;
        }

        return count.longValue() >= maxLoginLockedTimesUntilBlackListed.intValue();
    }


    private Long incrementCounter(String ip) {
        var key = this.getKey(ip);
        var attempts = this.redisTemplate.opsForValue().increment(key);
        if(attempts != null && attempts.equals(1L)) {
            this.redisTemplate.expire(key, CACHE_TTL);
        }

        return attempts;
    }

    private void invalidateCounter(String ip) {
        this.redisTemplate.delete(this.getKey(ip));
    }

    private String getKey(String ip) {
        return "login:black-listed:" + ip;
    }
}
