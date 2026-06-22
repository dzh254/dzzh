package com.cybersec.common.enums;

import lombok.Getter;

/**
 * 数据源/安全产品类型
 */
@Getter
public enum DataSourceType {

    SIEM("SIEM/SOC平台"),
    EDR("端点检测与响应"),
    WAF("Web应用防火墙"),
    NDR("网络检测与响应"),
    DLP("数据泄露防护"),
    VULN_SCANNER("漏洞扫描器"),
    THREAT_INTEL("威胁情报");

    private final String label;

    DataSourceType(String label) {
        this.label = label;
    }
}
