package com.aiocloud.test.license;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedisDistributedLock {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
    }

    public boolean releaseLock(String lockKey, String requestId) {
        String currentValue = redisTemplate.opsForValue().get(lockKey);
        if (requestId.equals(currentValue)) {
            redisTemplate.delete(lockKey);
            return true;
        }
        return false;
    }
}