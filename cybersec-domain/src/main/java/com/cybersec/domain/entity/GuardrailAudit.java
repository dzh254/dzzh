package com.cybersec.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 护栏审计日志 — t_guardrail_audit
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_guardrail_audit")
public class GuardrailAudit {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作用户ID */
    @TableField("user_id")
    private String userId;

    /** 操作描述 */
    private String operation;

    /** 操作级别: L0/L1/L2/L3 */
    private String level;

    /** 操作目标 */
    private String target;

    /** 护栏判断结果: ALLOWED/BLOCKED/PENDING_APPROVAL */
    private String result;

    /** 判断理由 */
    private String reasoning;

    /** 智能体原始输出 */
    @TableField("agent_output")
    private String agentOutput;

    /** 创建时间 */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
