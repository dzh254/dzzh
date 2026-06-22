package com.cybersec.api.controller;

import com.cybersec.api.dto.AlertIngestRequest;
import com.cybersec.api.dto.AlertQueryRequest;
import com.cybersec.common.dto.ApiResult;
import com.cybersec.common.dto.PageResult;
import com.cybersec.domain.entity.Alert;
import com.cybersec.domain.entity.AlertAnalysis;
import com.cybersec.domain.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 告警管理接口
 */
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Tag(name = "告警管理", description = "告警摄入、查询、分析相关接口")
public class AlertController {

    private final AlertService alertService;

    @PostMapping("/ingest")
    @Operation(summary = "摄入单条原始告警")
    public ApiResult<Alert> ingest(@RequestBody AlertIngestRequest request) {
        Alert alert = alertService.ingest(request.getRawData(), request.getSourceType());
        return ApiResult.success(alert);
    }

    @PostMapping("/ingest/batch")
    @Operation(summary = "批量摄入告警")
    public ApiResult<Map<String, Object>> ingestBatch(
            @Parameter(description = "告警列表") @RequestBody List<AlertIngestRequest> requests,
            @Parameter(description = "数据源类型") @RequestParam(defaultValue = "SIEM") String sourceType) {
        List<Map<String, Object>> rawAlerts = requests.stream()
                .map(AlertIngestRequest::getRawData)
                .toList();
        Map<String, Object> result = alertService.ingestBatch(rawAlerts, sourceType);
        return ApiResult.success(result);
    }

    @GetMapping
    @Operation(summary = "分页查询告警列表")
    public ApiResult<PageResult<Alert>> list(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        PageResult<Alert> result = alertService.list(source, severity, status, keyword, page, size);
        return ApiResult.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取告警详情")
    public ApiResult<Alert> getById(@PathVariable Long id) {
        return ApiResult.success(alertService.getById(id));
    }

    @PostMapping("/{id}/analyze")
    @Operation(summary = "触发AI分析")
    public ApiResult<AlertAnalysis> analyze(@PathVariable Long id) {
        return ApiResult.success(alertService.analyzeAlert(id));
    }

    @GetMapping("/{id}/analysis")
    @Operation(summary = "获取最新分析结果")
    public ApiResult<AlertAnalysis> getAnalysis(@PathVariable Long id) {
        return ApiResult.success(alertService.getLatestAnalysis(id));
    }

    @GetMapping("/stats")
    @Operation(summary = "获取告警统计")
    public ApiResult<Map<String, Object>> getStats() {
        return ApiResult.success(alertService.getStats());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新告警状态")
    public ApiResult<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        alertService.updateStatus(id, status);
        return ApiResult.success();
    }
}
