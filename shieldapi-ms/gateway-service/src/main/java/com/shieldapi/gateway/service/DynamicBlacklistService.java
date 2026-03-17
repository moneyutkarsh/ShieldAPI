package com.shieldapi.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DynamicBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "ip-blacklist:";

    public DynamicBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistIp(String ip, int minutes) {
        log.warn("Adding IP to Redis Blacklist: {} for {} minutes", ip, minutes);
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + ip, "true", minutes, TimeUnit.MINUTES);
    }

    public boolean isBlacklisted(String ip) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + ip));
    }

    public void clearBlacklist() {
        // Caution: This is expensive in Redis, but for completeness:
        java.util.Set<String> keys = redisTemplate.keys(BLACKLIST_PREFIX + "*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }
}
