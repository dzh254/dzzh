package com.cybersec.llm.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 结构化输出解析器 — 从LLM输出中提取JSON
 *
 * 处理常见LLM输出格式问题:
 * - Markdown code fences (```json ... ```)
 * - 不完整JSON (尝试闭合括号)
 * - 尾部逗号
 * - 混合文本中的JSON块
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StructuredOutputParser {

    private final ObjectMapper objectMapper;

    private static final Pattern JSON_FENCE_PATTERN = Pattern.compile(
            "```(?:json)?\\s*\\n?([\\s\\S]*?)\\n?```", Pattern.CASE_INSENSITIVE);
    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile(
            "\\{[^{}]*(?:\\{[^{}]*\\}[^{}]*)*\\}");

    /**
     * 从LLM输出文本中提取并解析JSON对象
     */
    public JsonNode extractJson(String llmOutput) {
        if (llmOutput == null || llmOutput.isBlank()) {
            throw new IllegalArgumentException("Empty LLM output");
        }

        String jsonStr = extractJsonString(llmOutput);

        try {
            return objectMapper.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            // 尝试修复常见问题
            String fixed = attemptFix(jsonStr);
            try {
                return objectMapper.readTree(fixed);
            } catch (JsonProcessingException ex) {
                log.error("Failed to parse JSON from LLM output. Original: {}", llmOutput.substring(0, Math.min(200, llmOutput.length())));
                throw new RuntimeException("Cannot parse structured output from LLM response", ex);
            }
        }
    }

    /**
     * 从LLM输出中提取JSON字符串
     */
    public String extractJsonString(String llmOutput) {
        // 1. 尝试提取 markdown code fence
        Matcher fenceMatcher = JSON_FENCE_PATTERN.matcher(llmOutput);
        if (fenceMatcher.find()) {
            return fenceMatcher.group(1).trim();
        }

        // 2. 查找第一个 { 到最后一个 }
        int start = llmOutput.indexOf('{');
        int end = llmOutput.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return llmOutput.substring(start, end + 1).trim();
        }

        // 3. 回退返回原始输出
        return llmOutput.trim();
    }

    /**
     * 将LLM输出解析为指定类型的Java对象
     */
    public <T> T parseAs(String llmOutput, Class<T> clazz) {
        JsonNode node = extractJson(llmOutput);
        try {
            return objectMapper.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot convert parsed JSON to " + clazz.getSimpleName(), e);
        }
    }

    /**
     * 尝试修复不完整或格式有问题的JSON
     */
    String attemptFix(String jsonStr) {
        String fixed = jsonStr;

        // 移除尾部逗号: "key": value, } → "key": value }
        fixed = fixed.replaceAll(",(\\s*[}\\]])", "$1");

        // 移除注释 (// 和 /* */)
        fixed = fixed.replaceAll("//[^\n]*", "");
        fixed = fixed.replaceAll("/\\*[\\s\\S]*?\\*/", "");

        // 尝试闭合未完成的字符串值
        int quoteCount = 0;
        boolean inString = false;
        for (char c : fixed.toCharArray()) {
            if (c == '"') {
                if (!inString) inString = true;
                else inString = false;
            }
        }

        // 统计括号数量
        int openBraces = fixed.length() - fixed.replace("{", "").length();
        int closeBraces = fixed.length() - fixed.replace("}", "").length();
        int openBrackets = fixed.length() - fixed.replace("[", "").length();
        int closeBrackets = fixed.length() - fixed.replace("]", "").length();

        // 补全缺失的括号
        StringBuilder sb = new StringBuilder(fixed);
        for (int i = 0; i < openBrackets - closeBrackets; i++) {
            sb.append("]");
        }
        for (int i = 0; i < openBraces - closeBraces; i++) {
            sb.append("}");
        }

        return sb.toString();
    }
}
