package com.cybersec.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 告警实体 — t_alert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_alert")
public class Alert {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 源系统告警唯一ID */
    @TableField("alert_id")
    private String alertId;

    /** 数据来源: SIEM/EDR/WAF/NDR/DLP 等 */
    private String source;

    /** 告警标题 */
    private String title;

    /** 告警描述 (TEXT) */
    private String description;

    /** 严重级别: CRITICAL/HIGH/MEDIUM/LOW/INFO */
    private String severity;

    /** 处理状态: PENDING/ANALYZING/ANALYZED/DISMISSED/RESOLVED */
    private String status;

    /** 原始告警JSON */
    @TableField("raw_data")
    private String rawData;

    /** ECS/OCSF标准化后的JSON */
    @TableField("normalized_data")
    private String normalizedData;

    /** 源IP */
    @TableField("source_ip")
    private String sourceIp;

    /** 目标IP */
    @TableField("dest_ip")
    private String destIp;

    /** 攻击类型 */
    @TableField("attack_type")
    private String attackType;

    /** ATT&CK 技战术ID (如 T1190) */
    @TableField("attck_id")
    private String attackId;

    /** 风险评分 (0-100) */
    @TableField("risk_score")
    private BigDecimal riskScore;

    /** 置信度 (0-100) */
    private BigDecimal confidence;

    /** 去重键 */
    @TableField("dedup_key")
    private String dedupKey;

    /** 指派分析师 */
    @TableField("assigned_to")
    private String assignedTo;

    /** 处置时间 */
    @TableField("resolved_at")
    private LocalDateTime resolvedAt;

    /** 创建时间 */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
