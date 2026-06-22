package com.cybersec.llm.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 通用对话请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    /** 模型名称 (如 claude-sonnet-4-6) */
    private String model;

    /** 消息列表 */
    private List<Message> messages;

    /** 温度 (0.0-1.0) */
    private Double temperature;

    /** 最大输出Token数 */
    private Integer maxTokens;

    /** 工具定义 (JSON Schema) */
    private List<Map<String, Object>> tools;

    /** 是否流式输出 */
    private boolean stream;

    /** 扩展思考 (仅部分模型支持) */
    private boolean extendedThinking;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;    // system / user / assistant / tool
        private String content;
        private String name;    // tool name (for tool results)
        private String toolCallId;
    }
}
