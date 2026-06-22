package com.cybersec.domain.service.impl;

import com.cybersec.domain.service.AlertNormalizer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 告警标准化器工厂 — 根据数据源类型路由到合适的标准化器
 */
@Slf4j
@Component
public class AlertNormalizerFactory {

    private final List<AlertNormalizer> normalizers;
    private final Map<String, AlertNormalizer> registry = new HashMap<>();

    public AlertNormalizerFactory(List<AlertNormalizer> normalizers) {
        this.normalizers = normalizers;
    }

    @PostConstruct
    public void init() {
        for (AlertNormalizer normalizer : normalizers) {
            for (String source : normalizer.supportedSources()) {
                registry.put(source.toUpperCase(), normalizer);
                log.info("Registered normalizer {} for source type: {}", normalizer.getClass().getSimpleName(), source);
            }
        }
        log.info("AlertNormalizerFactory initialized with {} normalizers covering {} source types",
                normalizers.size(), registry.size());
    }

    /**
     * 根据数据源类型标准化原始告警
     * @param rawAlert   原始告警数据
     * @param sourceType 数据源类型 (SIEM/EDR/WAF 等)
     * @return 标准化后的告警 Map
     */
    public Map<String, Object> normalize(Map<String, Object> rawAlert, String sourceType) {
        if (sourceType == null || sourceType.isBlank()) {
            throw new IllegalArgumentException("sourceType must not be blank");
        }

        AlertNormalizer normalizer = registry.get(sourceType.toUpperCase());
        if (normalizer != null) {
            return normalizer.normalize(rawAlert, sourceType);
        }

        // 无匹配标准化器时，返回原始告警包裹在 event.original 中
        log.warn("No normalizer found for source type: {}, wrapping raw alert", sourceType);
        Map<String, Object> fallback = new LinkedHashMap<>();
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("module", "unknown");
        event.put("original", rawAlert);
        fallback.put("event", event);
        return fallback;
    }
}
