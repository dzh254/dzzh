package com.cybersec.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cybersec.common.dto.PageResult;
import com.cybersec.common.exception.BusinessException;
import com.cybersec.domain.entity.*;
import com.cybersec.domain.mapper.*;
import com.cybersec.domain.service.impl.AlertNormalizerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 告警核心服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertMapper alertMapper;
    private final AlertAnalysisMapper analysisMapper;
    private final AlertNormalizerFactory normalizerFactory;
    private final ObjectMapper objectMapper;

    /**
     * 摄入单条原始告警
     */
    @Transactional
    public Alert ingest(Map<String, Object> rawAlert, String sourceType) {
        // 1. 标准化
        Map<String, Object> normalized = normalizerFactory.normalize(rawAlert, sourceType);

        // 2. 提取关键字段
        String rawJson = toJson(rawAlert);
        String normalizedJson = toJson(normalized);

        Alert alert = Alert.builder()
                .alertId(generateAlertId(sourceType))
                .source(sourceType)
                .title(extractTitle(rawAlert, sourceType))
                .description(extractDescription(rawAlert))
                .severity(extractSeverity(normalized))
                .status("PENDING")
                .rawData(rawJson)
                .normalizedData(normalizedJson)
                .sourceIp(extractField(normalized, "source", "ip"))
                .destIp(extractField(normalized, "destination", "ip"))
                .attackType(inferAttackType(rawAlert, normalized))
                .build();

        alertMapper.insert(alert);
        log.info("Alert ingested: id={}, source={}, alertId={}", alert.getId(), sourceType, alert.getAlertId());
        return alert;
    }

    /**
     * 批量摄入
     */
    @Transactional
    public Map<String, Object> ingestBatch(List<Map<String, Object>> rawAlerts, String sourceType) {
        int success = 0;
        int failed = 0;
        for (Map<String, Object> rawAlert : rawAlerts) {
            try {
                ingest(rawAlert, sourceType);
                success++;
            } catch (Exception e) {
                log.error("Failed to ingest alert from {}: {}", sourceType, e.getMessage());
                failed++;
            }
        }
        return Map.of("total", rawAlerts.size(), "success", success, "failed", failed);
    }

    /**
     * 分页查询告警列表
     */
    public PageResult<Alert> list(String source, String severity, String status,
                                   String keyword, long page, long size) {
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        if (source != null && !source.isBlank()) {
            wrapper.eq(Alert::getSource, source);
        }
        if (severity != null && !severity.isBlank()) {
            wrapper.eq(Alert::getSeverity, severity);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(Alert::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Alert::getTitle, keyword);
        }
        wrapper.orderByDesc(Alert::getCreatedAt);

        Page<Alert> mpPage = alertMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(mpPage.getCurrent(), mpPage.getSize(),
                mpPage.getTotal(), mpPage.getRecords());
    }

    /**
     * 获取告警详情
     */
    public Alert getById(Long id) {
        Alert alert = alertMapper.selectById(id);
        if (alert == null) {
            throw BusinessException.notFound("告警不存在: id=" + id);
        }
        return alert;
    }

    /**
     * 触发AI分析（桩实现 — Stage 6 接入真实编排器）
     */
    @Transactional
    public AlertAnalysis analyzeAlert(Long alertId) {
        Alert alert = getById(alertId);

        // 更新状态为分析中
        alert.setStatus("ANALYZING");
        alertMapper.updateById(alert);

        long startMs = System.currentTimeMillis();

        // TODO: Stage 6 — 接入 SecurityAgentOrchestrator
        AlertAnalysis analysis = AlertAnalysis.builder()
                .alertId(alertId)
                .conclusion(analyzeStub(alert))
                .confidence(BigDecimal.valueOf(75))
                .attackType(alert.getAttackType())
                .riskLevel(alert.getSeverity())
                .evidence(toJson(List.of(
                        Map.of("type", "raw_alert", "source", alert.getSource())
                )))
                .recommendedActions(toJson(List.of(
                        Map.of("action", "进一步人工分析", "level", "L1")
                )))
                .reasoningChain("基于告警特征进行初步研判...")
                .modelUsed("placeholder")
                .tokensUsed(0)
                .latencyMs(System.currentTimeMillis() - startMs)
                .build();

        analysisMapper.insert(analysis);

        // 更新状态为已分析
        alert.setStatus("ANALYZED");
        alertMapper.updateById(alert);

        log.info("Alert analyzed: alertId={}, analysisId={}", alertId, analysis.getId());
        return analysis;
    }

    private String analyzeStub(Alert alert) {
        return String.format(
                "【初步研判】\n" +
                "告警来源: %s\n" +
                "严重级别: %s\n" +
                "攻击类型: %s\n" +
                "源IP: %s\n" +
                "\n本分析为桩实现，完整的LLM深度分析将在Stage 6接入。",
                alert.getSource(),
                alert.getSeverity(),
                alert.getAttackType() != null ? alert.getAttackType() : "未识别",
                alert.getSourceIp() != null ? alert.getSourceIp() : "未知"
        );
    }

    /**
     * 获取告警最新分析结果
     */
    public AlertAnalysis getLatestAnalysis(Long alertId) {
        AlertAnalysis analysis = analysisMapper.findLatestByAlertId(alertId);
        if (analysis == null) {
            throw BusinessException.notFound("该告警尚未进行分析: alertId=" + alertId);
        }
        return analysis;
    }

    /**
     * 更新告警状态
     */
    @Transactional
    public void updateStatus(Long id, String newStatus) {
        Set<String> validStatuses = Set.of("PENDING", "ANALYZING", "ANALYZED", "DISMISSED", "RESOLVED");
        if (!validStatuses.contains(newStatus)) {
            throw BusinessException.badRequest("无效的告警状态: " + newStatus);
        }
        Alert alert = getById(id);
        alert.setStatus(newStatus);
        if ("RESOLVED".equals(newStatus)) {
            alert.setResolvedAt(LocalDateTime.now());
        }
        alertMapper.updateById(alert);
        log.info("Alert status updated: id={}, status={}", id, newStatus);
    }

    /**
     * 获取告警统计
     */
    public Map<String, Object> getStats() {
        LocalDateTime since24h = LocalDateTime.now().minusHours(24);
        long total = alertMapper.countSince(since24h);
        List<Map<String, Object>> bySeverity = alertMapper.countBySeverity(since24h);
        List<Map<String, Object>> bySource = alertMapper.countBySource(since24h);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", total);
        stats.put("bySeverity", bySeverity);
        stats.put("bySource", bySource);
        stats.put("since", since24h.toString());
        return stats;
    }

    // ─── 辅助方法 ───

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("JSON serialization failed: {}", e.getMessage());
            return "{}";
        }
    }

    private String generateAlertId(String sourceType) {
        return sourceType.toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String extractTitle(Map<String, Object> rawAlert, String sourceType) {
        // 尝试提取常见标题字段
        for (String key : List.of("title", "event_name", "alert_name", "signature", "rule_name")) {
            Object val = rawAlert.get(key);
            if (val != null) return val.toString();
        }
        return sourceType + " Alert";
    }

    private String extractDescription(Map<String, Object> rawAlert) {
        Object desc = rawAlert.get("description");
        if (desc != null) return desc.toString();

        // 如果无描述字段，将 payload 作为描述
        Object payload = rawAlert.get("payload");
        if (payload != null) return "Payload: " + payload;

        return toJson(rawAlert);
    }

    private String extractSeverity(Map<String, Object> normalized) {
        @SuppressWarnings("unchecked")
        Map<String, Object> event = (Map<String, Object>) normalized.get("event");
        if (event != null) {
            Object severity = event.get("severity");
            if (severity != null) return severity.toString().toUpperCase();
        }
        return "MEDIUM";
    }

    @SuppressWarnings("unchecked")
    private String extractField(Map<String, Object> ecs, String parent, String field) {
        Object parentObj = ecs.get(parent);
        if (parentObj instanceof Map) {
            Object val = ((Map<String, Object>) parentObj).get(field);
            if (val != null) return val.toString();
        }
        return null;
    }

    private String inferAttackType(Map<String, Object> rawAlert, Map<String, Object> normalized) {
        // 从payload或规则名推测攻击类型
        String payload = Objects.toString(rawAlert.get("payload"), "").toLowerCase();
        String ruleName = Objects.toString(rawAlert.get("rule_name"), "").toLowerCase();

        if (payload.contains("select") || payload.contains("union") || payload.contains("sql")
                || ruleName.contains("sql")) {
            return "SQL Injection";
        }
        if (payload.contains("<script") || payload.contains("javascript") || ruleName.contains("xss")) {
            return "Cross-Site Scripting";
        }
        if (ruleName.contains("brute") || ruleName.contains("auth fail")) {
            return "Brute Force";
        }
        if (ruleName.contains("powershell") || ruleName.contains("suspicious process")) {
            return "Suspicious Process";
        }
        return null;
    }
}
