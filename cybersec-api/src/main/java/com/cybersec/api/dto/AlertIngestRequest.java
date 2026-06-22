package com.cybersec.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 告警摄入请求
 */
@Data
@Schema(description = "告警摄入请求")
public class AlertIngestRequest {

    @Schema(description = "数据源类型: SIEM/EDR/WAF", example = "WAF", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sourceType;

    @Schema(description = "原始告警数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> rawData;

    @Schema(description = "附加元数据")
    private String metadata;
}
