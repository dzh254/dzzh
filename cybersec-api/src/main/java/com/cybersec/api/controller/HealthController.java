package com.cybersec.api.controller;

import com.cybersec.common.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统健康检查接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "系统管理", description = "健康检查、配置信息")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private RedissonClient redissonClient;

    @GetMapping("/health")
    @Operation(summary = "系统健康检查")
    public ApiResult<Map<String, Object>> health() {
        Map<String, Object> healthMap = new LinkedHashMap<>();
        healthMap.put("status", "UP");
        healthMap.put("timestamp", System.currentTimeMillis());

        Map<String, Object> components = new LinkedHashMap<>();
        components.put("db", checkDatabase());
        components.put("redis", checkRedis());
        components.put("app", Map.of("status", "UP", "name", "cybersec-platform", "version", "1.0.0-SNAPSHOT"));

        healthMap.put("components", components);
        return ApiResult.success(healthMap);
    }

    private Map<String, Object> checkDatabase() {
        Map<String, Object> result = new LinkedHashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            result.put("status", "UP");
            result.put("database", conn.getCatalog());
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
        }
        return result;
    }

    private Map<String, Object> checkRedis() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            if (redissonClient != null && !redissonClient.isShutdown()) {
                redissonClient.getKeys().count();
                result.put("status", "UP");
            } else {
                result.put("status", "DOWN");
                result.put("error", "Redis client not available");
            }
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
        }
        return result;
    }

    @GetMapping("/config")
    @Operation(summary = "获取系统公共配置")
    public ApiResult<Map<String, Object>> config() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("appName", "通用网络安全智能体平台");
        config.put("version", "1.0.0-SNAPSHOT");
        config.put("phase", "MVP Phase 1");
        config.put("availableModels", java.util.List.of(
                Map.of("name", "Claude Opus 4", "provider", "Anthropic", "type", "deep-analysis"),
                Map.of("name", "Claude Sonnet", "provider", "Anthropic", "type", "balanced"),
                Map.of("name", "DeepSeek-V3", "provider", "DeepSeek", "type", "fast-classify")
        ));
        return ApiResult.success(config);
    }
}
