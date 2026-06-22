package com.cybersec.infra.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis 缓存服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    private boolean isAvailable() {
        return redissonClient != null && !redissonClient.isShutdown();
    }

    public <T> T get(String key, Class<T> clazz) {
        if (!isAvailable()) return null;
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            String json = bucket.get();
            if (json == null) return null;
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("Redis get failed: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    public String getRaw(String key) {
        if (!isAvailable()) return null;
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            return bucket.get();
        } catch (Exception e) {
            log.warn("Redis getRaw failed: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    public void set(String key, Object value, long ttlSeconds) {
        if (!isAvailable()) return;
        try {
            String json = objectMapper.writeValueAsString(value);
            RBucket<String> bucket = redissonClient.getBucket(key);
            bucket.set(json, Duration.ofSeconds(ttlSeconds));
        } catch (JsonProcessingException e) {
            log.warn("Redis set serialization failed: key={}, error={}", key, e.getMessage());
        }
    }

    public void delete(String key) {
        if (!isAvailable()) return;
        try {
            redissonClient.getBucket(key).delete();
        } catch (Exception e) {
            log.warn("Redis delete failed: key={}, error={}", key, e.getMessage());
        }
    }

    public boolean exists(String key) {
        if (!isAvailable()) return false;
        try {
            return redissonClient.getBucket(key).isExists();
        } catch (Exception e) {
            log.warn("Redis exists failed: key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    public boolean setIfAbsent(String key, Object value, long ttlSeconds) {
        if (!isAvailable()) return false;
        try {
            String json = objectMapper.writeValueAsString(value);
            RBucket<String> bucket = redissonClient.getBucket(key);
            return bucket.setIfAbsent(json, Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            log.warn("Redis setIfAbsent failed: key={}, error={}", key, e.getMessage());
            return false;
        }
    }
}
