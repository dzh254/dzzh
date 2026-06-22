package com.cybersec.llm.gateway;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * LLM 模型网关接口 — 统一的模型抽象层，支持可插拔模型切换
 */
public interface ModelGateway {

    /**
     * 同步对话
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 流式对话 — 返回文本流
     */
    Stream<String> chatStream(ChatRequest request);

    /**
     * 异步对话
     */
    CompletableFuture<ChatResponse> chatAsync(ChatRequest request);

    /**
     * 模型提供商名称
     */
    String providerName();

    /**
     * 支持的模型列表
     */
    java.util.List<String> supportedModels();

    /**
     * 健康检查
     */
    boolean isAvailable();
}
