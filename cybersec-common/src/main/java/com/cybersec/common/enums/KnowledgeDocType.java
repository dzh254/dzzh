package com.cybersec.common.enums;

import lombok.Getter;

/**
 * 知识文档类型
 */
@Getter
public enum KnowledgeDocType {

    SOP("标准处置流程"),
    CVE("CVE漏洞"),
    ATTACK_CASE("攻击案例"),
    THREAT_INTEL("威胁情报"),
    SECURITY_REPORT("安全研究报告"),
    BEST_PRACTICE("最佳实践");

    private final String label;

    KnowledgeDocType(String label) {
        this.label = label;
    }
}
