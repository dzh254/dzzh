package com.cybersec.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 威胁情报IOC — t_threat_intel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_threat_intel")
public class ThreatIntel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** IOC类型: IP/DOMAIN/HASH/URL */
    @TableField("ioc_type")
    private String iocType;

    /** IOC值 */
    @TableField("ioc_value")
    private String iocValue;

    /** 威胁级别 */
    @TableField("threat_level")
    private String threatLevel;

    /** 标签 */
    private String tags;

    /** 恶意软件家族 */
    private String family;

    /** 情报来源 */
    private String source;

    /** 首次发现时间 */
    @TableField("first_seen")
    private LocalDateTime firstSeen;

    /** 最近发现时间 */
    @TableField("last_seen")
    private LocalDateTime lastSeen;

    /** 置信度 */
    private Integer confidence;

    /** 创建时间 */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
