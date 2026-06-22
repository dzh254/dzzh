package com.cybersec.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis (Redisson) 配置 — 支持优雅降级
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = "redis://" + host + ":" + port;
        config.useSingleServer().setAddress(address);

        if (password != null && !password.isBlank()) {
            config.useSingleServer().setPassword(password);
        }

        try {
            RedissonClient client = Redisson.create(config);
            // 测试连接
            client.getKeys().count();
            log.info("Redis (Redisson) connected: {}", address);
            return client;
        } catch (Exception e) {
            log.warn("Redis unavailable at {} — running without cache: {}", address, e.getMessage());
            // 返回 null, 业务层需判空
            return null;
        }
    }
}
