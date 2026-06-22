package com.cybersec.llm.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * DeepSeek 模型网关 — OpenAI-compatible API
 */
@Slf4j
@Component("deepseekGateway")
public class DeepSeekModelGateway implements ModelGateway {

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.base.url:https://api.deepseek.com}")
    private String baseUrl;

    @Override
    public ChatResponse chat(ChatRequest request) {
        log.debug("DeepSeek chat stub: model={}", request.getModel());
        return ChatResponse.builder()
                .content("【DeepSeek分析桩】快速分类模型集成中...")
                .finishReason("stop")
                .model(request.getModel() != null ? request.getModel() : "deepseek-chat")
                .latencyMs(0)
                .tokenUsage(ChatResponse.TokenUsage.builder()
                        .inputTokens(0).outputTokens(0).totalTokens(0).build())
                .build();
    }

    @Override
    public Stream<String> chatStream(ChatRequest request) {
        return Stream.of("DeepSeek streaming stub — pending");
    }

    @Override
    public CompletableFuture<ChatResponse> chatAsync(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> chat(request));
    }

    @Override
    public String providerName() {
        return "DeepSeek";
    }

    @Override
    public List<String> supportedModels() {
        return List.of("deepseek-chat", "deepseek-reasoner");
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }
}
