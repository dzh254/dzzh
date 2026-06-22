package com.cybersec.domain.service.impl;

import com.cybersec.domain.service.AlertNormalizer;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * WAF 告警标准化器
 */
@Component
public class WafAlertNormalizer implements AlertNormalizer {

    @Override
    public Map<String, Object> normalize(Map<String, Object> rawAlert, String sourceType) {
        Map<String, Object> ecs = new LinkedHashMap<>();

        // event 对象
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("category", List.of("intrusion_detection"));
        event.put("type", List.of("alert"));
        event.put("module", "waf");
        event.put("action", rawAlert.getOrDefault("action", "unknown"));
        event.put("original", rawAlert);
        ecs.put("event", event);

        // source 对象
        if (rawAlert.containsKey("src_ip")) {
            Map<String, Object> source = new LinkedHashMap<>();
            source.put("ip", rawAlert.get("src_ip"));
            ecs.put("source", source);
        }

        // url 对象
        if (rawAlert.containsKey("url_path")) {
            Map<String, Object> url = new LinkedHashMap<>();
            url.put("path", rawAlert.get("url_path"));
            ecs.put("url", url);
        }

        // http 对象
        Map<String, Object> http = new LinkedHashMap<>();
        if (rawAlert.containsKey("http_method")) {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("method", rawAlert.get("http_method"));
            http.put("request", request);
        }
        if (!http.isEmpty()) {
            ecs.put("http", http);
        }

        // user_agent 对象
        if (rawAlert.containsKey("user_agent")) {
            Map<String, Object> ua = new LinkedHashMap<>();
            ua.put("original", rawAlert.get("user_agent"));
            ecs.put("user_agent", ua);
        }

        // rule 对象
        Map<String, Object> rule = new LinkedHashMap<>();
        if (rawAlert.containsKey("rule_id")) {
            rule.put("id", rawAlert.get("rule_id"));
        }
        if (rawAlert.containsKey("rule_name")) {
            rule.put("name", rawAlert.get("rule_name"));
        }
        if (!rule.isEmpty()) {
            ecs.put("rule", rule);
        }

        return ecs;
    }

    @Override
    public Set<String> supportedSources() {
        return Set.of("WAF");
    }
}
