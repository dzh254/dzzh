package com.cybersec.common.enums;

import lombok.Getter;

/**
 * 告警严重级别
 */
@Getter
public enum AlertSeverity {

    CRITICAL("严重", 5),
    HIGH("高", 4),
    MEDIUM("中", 3),
    LOW("低", 2),
    INFO("信息", 1);

    private final String label;
    private final int level;

    AlertSeverity(String label, int level) {
        this.label = label;
        this.level = level;
    }

    /**
     * 根据数值级别获取枚举
     */
    public static AlertSeverity fromLevel(int level) {
        for (AlertSeverity s : values()) {
            if (s.level == level) return s;
        }
        return INFO;
    }

    /**
     * 根据字符串名称模糊匹配（兼容不同系统的命名差异）
     */
    public static AlertSeverity fromName(String name) {
        if (name == null) return INFO;
        String upper = name.toUpperCase().trim();
        for (AlertSeverity s : values()) {
            if (s.name().equals(upper) || s.label.equals(name)) return s;
        }
        // 尝试模糊匹配
        if (upper.contains("CRIT") || upper.contains("严重")) return CRITICAL;
        if (upper.contains("HIGH") || upper.contains("高")) return HIGH;
        if (upper.contains("MED") || upper.contains("中")) return MEDIUM;
        if (upper.contains("LOW") || upper.contains("低")) return LOW;
        return INFO;
    }
}
