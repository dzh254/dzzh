package com.cybersec.llm.prompt;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 提示词注册中心 — 从 classpath:/prompts/ 加载提示词模板
 *
 * 模板文件格式:
 * ---
 * SYSTEM PROMPT (到 ===USER=== 之前的所有内容)
 * ===USER===
 * USER PROMPT TEMPLATE (支持 {{variable}} 变量)
 */
@Slf4j
@Component
public class PromptRegistry {

    private final Map<String, PromptTemplate> templates = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/prompts/*.txt");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null) continue;

                String name = filename.replace(".txt", "");
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

                String[] parts = content.split("===USER===", 2);
                String systemPrompt = parts.length >= 1 ? parts[0].trim() : "";
                String userTemplate = parts.length >= 2 ? parts[1].trim() : "";

                PromptTemplate template = new PromptTemplate(name, systemPrompt, userTemplate);
                templates.put(name, template);
                log.info("Loaded prompt template: {}", name);
            }
            log.info("PromptRegistry initialized: {} templates loaded", templates.size());
        } catch (IOException e) {
            log.warn("Failed to load prompt templates: {}", e.getMessage());
        }
    }

    /**
     * 获取指定模板
     */
    public PromptTemplate get(String name) {
        PromptTemplate template = templates.get(name);
        if (template == null) {
            throw new IllegalArgumentException("Prompt template not found: " + name);
        }
        return template;
    }

    /**
     * 是否存在指定模板
     */
    public boolean hasTemplate(String name) {
        return templates.containsKey(name);
    }

    /**
     * 获取所有注册的模板名称
     */
    public java.util.Set<String> templateNames() {
        return templates.keySet();
    }
}
