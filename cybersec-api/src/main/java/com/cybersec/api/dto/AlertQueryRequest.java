package com.cybersec.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 告警分页查询请求
 */
@Data
@Schema(description = "告警分页查询请求")
public class AlertQueryRequest {

    @Schema(description = "数据源")
    private String source;

    @Schema(description = "严重级别")
    private String severity;

    @Schema(description = "处理状态")
    private String status;

    @Schema(description = "关键词搜索")
    private String keyword;

    @Schema(description = "页码", defaultValue = "1")
    private long page = 1;

    @Schema(description = "每页大小", defaultValue = "10")
    private long size = 10;
}
