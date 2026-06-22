package com.cybersec.llm.gateway;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Claude 模型网关 — LangChain4j Anthropic 集成
 */
@Slf4j
@Component("claudeGateway")
public class ClaudeModelGateway implements ModelGateway {

    @Value("${anthropic.api.key:}")
    private String apiKey;

    @Value("${anthropic.base.url:}")
    private String baseUrl;

    @Override
    public ChatResponse chat(ChatRequest request) {
        // TODO: Full LangChain4j integration — Phase 2
        // For now, return a stub response
        log.debug("Claude chat stub: model={}, messages={}", request.getModel(), request.getMessages().size());
        return ChatResponse.builder()
                .content("【Claude分析桩】LLM推理模块正在集成中...\n" +
                         "告警分析将基于深度推理模型提供：\n" +
                         "1. 攻击类型识别与ATT&CK映射\n" +
                         "2. 威胁严重级别评估\n" +
                         "3. 证据链提取\n" +
                         "4. 处置建议生成")
                .finishReason("stop")
                .model(request.getModel() != null ? request.getModel() : "claude-haiku")
                .latencyMs(0)
                .tokenUsage(ChatResponse.TokenUsage.builder()
                        .inputTokens(0).outputTokens(0).totalTokens(0).build())
                .build();
    }

    @Override
    public Stream<String> chatStream(ChatRequest request) {
        return Stream.of("Claude streaming analysis stub — Phase 2 integration pending");
    }

    @Override
    public CompletableFuture<ChatResponse> chatAsync(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> chat(request));
    }

    @Override
    public String providerName() {
        return "Anthropic";
    }

    @Override
    public List<String> supportedModels() {
        return List.of("claude-opus-4-8", "claude-sonnet-4-6", "claude-haiku-4-5");
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }
}
