package com.cybersec.llm.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 通用对话响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /** 响应的文本内容 */
    private String content;

    /** 工具调用请求 */
    private List<ToolCall> toolCalls;

    /** Token用量统计 */
    private TokenUsage tokenUsage;

    /** 结束原因: stop / tool_calls / length / error */
    private String finishReason;

    /** 模型名称 */
    private String model;

    /** 响应延迟 (ms) */
    private long latencyMs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCall {
        private String id;
        private String name;
        private Map<String, Object> arguments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenUsage {
        private int inputTokens;
        private int outputTokens;
        private int totalTokens;
    }
}
