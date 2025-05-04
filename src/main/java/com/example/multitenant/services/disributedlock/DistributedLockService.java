package com.example.multitenant.services.disributedlock;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * we are applying locks sharding based on the tenant id 
 * therefore the same tenant/organization will use same lock all the time.
 * which ensures no spam passing the limit on categories/categories-channels
 * or members count in case too many invitations were accepted at same time.
 */
// TODO: adding a unique value to the locks to ensure only the intended lock will be unlocked and not the newly locked.
@Slf4j
@RequiredArgsConstructor
@Service
public class DistributedLockService {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean acquireLock(Integer tenantId, String resourceType, long lockTimeout, TimeUnit timeUnit) {
        var lockKey = getKey(tenantId, resourceType);
        var isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", lockTimeout, timeUnit);
        return Boolean.TRUE.equals(isLocked);
    }

    public void releaseLock(Integer tenantId, String resourceType) {
        var lockKey = getKey(tenantId, resourceType);
        redisTemplate.delete(lockKey);
    }

    public String getKey(Integer tenantId, String resourceType) {
        if(resourceType == null || resourceType.isEmpty()) {
            var errMsg = "resource-type is required for locking";
            log.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        return String.format("locks:org:%s:resource-type:%s", tenantId, resourceType);
    }

    public String getTenantKey(Integer tenantId, String handler) {
        return String.format("locks:org:tenant-lock:%s:%s", handler, tenantId);
    }
}