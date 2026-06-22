package com.cybersec.llm.parser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 告警分析的结构化输出 Schema
 *
 * 期望LLM按此格式输出JSON:
 * {
 *   "conclusion": "分析结论",
 *   "confidence": 85.5,
 *   "attack_type": "SQL Injection",
 *   "attack_id": "T1190",
 *   "risk_level": "HIGH",
 *   "evidence": [{"type": "...", "detail": "..."}],
 *   "recommended_actions": [{"action": "...", "level": "L2", "reason": "..."}],
 *   "reasoning_chain": "推理过程..."
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertAnalysisSchema {

    /** 分析结论文本 */
    private String conclusion;

    /** 置信度 (0-100) */
    private Double confidence;

    /** 攻击类型 */
    @JsonProperty("attack_type")
    private String attackType;

    /** ATT&CK 技战术ID */
    @JsonProperty("attack_id")
    private String attackId;

    /** 风险等级: CRITICAL/HIGH/MEDIUM/LOW/INFO */
    @JsonProperty("risk_level")
    private String riskLevel;

    /** 证据列表 */
    private List<EvidenceItem> evidence;

    /** 建议处置动作 */
    @JsonProperty("recommended_actions")
    private List<RecommendedAction> recommendedActions;

    /** 推理链文本 */
    @JsonProperty("reasoning_chain")
    private String reasoningChain;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenceItem {
        /** 证据类型 */
        private String type;
        /** 证据详情 */
        private String detail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedAction {
        /** 处置动作 */
        private String action;
        /** 操作级别 (L0-L3) */
        private String level;
        /** 原因说明 */
        private String reason;
    }
}
