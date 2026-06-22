package com.cybersec.common.enums;

import lombok.Getter;

/**
 * 告警处理状态
 */
@Getter
public enum AlertStatus {

    PENDING("待处理"),
    ANALYZING("分析中"),
    ANALYZED("已分析"),
    DISMISSED("已忽略"),
    RESOLVED("已处置");

    private final String label;

    AlertStatus(String label) {
        this.label = label;
    }
}
