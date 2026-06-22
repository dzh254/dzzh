package com.cybersec.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 告警分析结果 — t_alert_analysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_alert_analysis")
public class AlertAnalysis {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联告警ID */
    @TableField("alert_id")
    private Long alertId;

    /** 分析结论文本 */
    private String conclusion;

    /** 分析置信度 */
    private BigDecimal confidence;

    /** 确定的攻击类型 */
    @TableField("attack_type")
    private String attackType;

    /** ATT&CK 技战术ID */
    @TableField("attck_id")
    private String attackId;

    /** 风险等级 */
    @TableField("risk_level")
    private String riskLevel;

    /** 证据列表 (JSON数组) */
    private String evidence;

    /** 建议处置动作 (JSON数组) */
    @TableField("recommended_actions")
    private String recommendedActions;

    /** 推理链文本 */
    @TableField("reasoning_chain")
    private String reasoningChain;

    /** 使用的模型 */
    @TableField("model_used")
    private String modelUsed;

    /** 消耗Token数 */
    @TableField("tokens_used")
    private Integer tokensUsed;

    /** 分析延迟 (ms) */
    @TableField("latency_ms")
    private Long latencyMs;

    /** 创建时间 */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
