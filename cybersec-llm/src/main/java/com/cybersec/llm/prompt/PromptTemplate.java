package com.cybersec.llm.prompt;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提示词模板引擎 — 支持 {{variable}} 替换
 */
public class PromptTemplate {

    private static final Pattern VAR_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    private final String name;
    private final String systemPrompt;
    private final String userPromptTemplate;

    public PromptTemplate(String name, String systemPrompt, String userPromptTemplate) {
        this.name = name;
        this.systemPrompt = systemPrompt;
        this.userPromptTemplate = userPromptTemplate;
    }

    /**
     * 使用变量填充用户提示词模板
     */
    public String render(Map<String, String> variables) {
        String result = userPromptTemplate;
        Matcher matcher = VAR_PATTERN.matcher(result);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = variables.getOrDefault(varName, "{{" + varName + "}}");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 构建完整的系统提示词（可能也包含变量）
     */
    public String renderSystem(Map<String, String> variables) {
        String result = systemPrompt;
        Matcher matcher = VAR_PATTERN.matcher(result);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = variables.getOrDefault(varName, "{{" + varName + "}}");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getUserPromptTemplate() {
        return userPromptTemplate;
    }

    @Override
    public String toString() {
        return "PromptTemplate{" + "name='" + name + '\'' + '}';
    }
}
