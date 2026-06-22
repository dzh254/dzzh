package com.cybersec.llm.gateway;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 模型网关路由器 — 根据任务复杂度自动选择模型
 *
 * 路由策略:
 * - 分类/打分/初筛 → 轻量模型 (DeepSeek-V3)
 * - 深度研判/根因分析 → 强推理模型 (Claude Opus)
 * - 常规分析 → 平衡型 (Claude Sonnet)
 */
@Slf4j
@Component
public class ModelGatewayRouter {

    private final List<ModelGateway> gateways;
    private final Map<String, ModelGateway> gatewayMap = new HashMap<>();
    private ModelGateway defaultGateway;

    public ModelGatewayRouter(List<ModelGateway> gateways) {
        this.gateways = gateways;
    }

    @PostConstruct
    public void init() {
        for (ModelGateway gw : gateways) {
            for (String model : gw.supportedModels()) {
                gatewayMap.put(model, gw);
            }
            if (defaultGateway == null || gw.isAvailable()) {
                defaultGateway = gw;
            }
        }
        log.info("ModelGatewayRouter initialized: {} gateways, {} models",
                gateways.size(), gatewayMap.size());
    }

    /**
     * 根据模型名路由
     */
    public ModelGateway routeByModel(String modelName) {
        ModelGateway gw = gatewayMap.get(modelName);
        if (gw != null && gw.isAvailable()) {
            return gw;
        }
        log.warn("Model '{}' not found or unavailable, using default", modelName);
        return defaultGateway;
    }

    /**
     * 根据任务类型路由
     */
    public ModelGateway routeByTask(TaskType taskType) {
        return switch (taskType) {
            case FAST_CLASSIFY, ALERT_TRIAGE -> findAvailable("deepseek-chat")
                    .orElse(defaultGateway);
            case DEEP_ANALYSIS, ROOT_CAUSE, THREAT_HUNTING -> findAvailable("claude-opus-4-8")
                    .or(() -> findAvailable("claude-sonnet-4-6"))
                    .orElse(defaultGateway);
            case STANDARD_ANALYSIS, REPORT_GEN -> findAvailable("claude-sonnet-4-6")
                    .or(() -> findAvailable("claude-haiku-4-5"))
                    .orElse(defaultGateway);
        };
    }

    private Optional<ModelGateway> findAvailable(String modelName) {
        ModelGateway gw = gatewayMap.get(modelName);
        if (gw != null && gw.isAvailable()) {
            return Optional.of(gw);
        }
        return Optional.empty();
    }

    public List<ModelGateway> allGateways() {
        return List.copyOf(gateways);
    }

    public Map<String, Boolean> modelAvailability() {
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (ModelGateway gw : gateways) {
            for (String model : gw.supportedModels()) {
                result.put(model, gw.isAvailable());
            }
        }
        return result;
    }

    /**
     * 任务类型枚举
     */
    public enum TaskType {
        /** 快速分类/告警初筛 */
        FAST_CLASSIFY,
        /** 告警初筛 */
        ALERT_TRIAGE,
        /** 标准深度分析 */
        STANDARD_ANALYSIS,
        /** 深度研判 */
        DEEP_ANALYSIS,
        /** 根因分析 */
        ROOT_CAUSE,
        /** 威胁狩猎 */
        THREAT_HUNTING,
        /** 报告生成 */
        REPORT_GEN
    }
}
