package com.cybersec.common.enums;

import lombok.Getter;

/**
 * 安全操作分级模型 (L0-L3)
 *
 * L0: 只读查询 — 无需确认，自动执行
 * L1: 建议输出 — 自动输出分析/建议，不执行实际操作
 * L2: 低危自动 — 自动执行 + 事后通知 + 可回滚
 * L3: 高危确认 — 生成操作票 → 人工审批 → 执行 → 验证
 */
@Getter
public enum OperationLevel {

    L0(0, "只读查询", false, false),
    L1(1, "建议输出", false, false),
    L2(2, "低危自动", true, false),
    L3(3, "高危确认", true, true);

    private final int level;
    private final String label;
    /** 是否为写操作 */
    private final boolean isWrite;
    /** 是否需要人工审批 */
    private final boolean requireApproval;

    OperationLevel(int level, String label, boolean isWrite, boolean requireApproval) {
        this.level = level;
        this.label = label;
        this.isWrite = isWrite;
        this.requireApproval = requireApproval;
    }

    public static OperationLevel fromLevel(int level) {
        for (OperationLevel ol : values()) {
            if (ol.level == level) return ol;
        }
        throw new IllegalArgumentException("Unknown operation level: " + level);
    }
}
