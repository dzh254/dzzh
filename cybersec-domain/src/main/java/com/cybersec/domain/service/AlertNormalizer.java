package com.cybersec.domain.service;

import java.util.Map;
import java.util.Set;

/**
 * 告警标准化接口 — 将不同数据源的原始告警转换为 ECS/OCSF 统一格式
 */
public interface AlertNormalizer {

    /**
     * 将原始告警标准化为 ECS/OCSF 格式
     * @param rawAlert  原始告警数据
     * @param sourceType 数据源类型 (SIEM/EDR/WAF等)
     * @return 标准化后的告警 Map
     */
    Map<String, Object> normalize(Map<String, Object> rawAlert, String sourceType);

    /**
     * 返回该标准化器支持的数据源类型
     */
    Set<String> supportedSources();
}
