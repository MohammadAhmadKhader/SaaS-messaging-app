package com.example.multitenant.services.security;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class RateLimiterService {
    private String prefix = "spring:java-app:bucket:v1:";
    @Autowired
    private ProxyManager<String> proxyManager;

    private Supplier<BucketConfiguration> getNormalUsersConfig() {
        return ()-> BucketConfiguration.builder()
            .addLimit((r)-> {
                return r.capacity(200L).refillGreedy(200, Duration.ofMinutes(1L));
            })
            .build();
    }
    
    public Bucket getBucketByIP(String ip) {
        return (Bucket) proxyManager.builder().build(prefix + ip, getNormalUsersConfig());
    }
}