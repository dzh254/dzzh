package com.cybersec.domain.service.impl;

import com.cybersec.domain.service.AlertNormalizer;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * SIEM 告警标准化器
 */
@Component
public class SiemAlertNormalizer implements AlertNormalizer {

    @Override
    public Map<String, Object> normalize(Map<String, Object> rawAlert, String sourceType) {
        Map<String, Object> ecs = new LinkedHashMap<>();

        // event 对象
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("category", List.of("intrusion_detection"));
        event.put("type", List.of("alert"));
        event.put("module", "siem");
        if (rawAlert.containsKey("event_name")) {
            event.put("action", rawAlert.get("event_name"));
        }
        event.put("original", rawAlert);
        ecs.put("event", event);

        // source 对象
        if (rawAlert.containsKey("src_ip")) {
            Map<String, Object> source = new LinkedHashMap<>();
            source.put("ip", rawAlert.get("src_ip"));
            ecs.put("source", source);
        }

        // destination 对象
        if (rawAlert.containsKey("dest_ip")) {
            Map<String, Object> destination = new LinkedHashMap<>();
            destination.put("ip", rawAlert.get("dest_ip"));
            ecs.put("destination", destination);
        }

        // rule 对象
        Map<String, Object> rule = new LinkedHashMap<>();
        if (rawAlert.containsKey("rule_name")) {
            rule.put("name", rawAlert.get("rule_name"));
        }
        if (!rule.isEmpty()) {
            ecs.put("rule", rule);
        }

        // host 对象
        if (rawAlert.containsKey("host")) {
            Map<String, Object> host = new LinkedHashMap<>();
            Object hostVal = rawAlert.get("host");
            if (hostVal instanceof Map) {
                ecs.put("host", hostVal);
            } else {
                host.put("name", hostVal.toString());
                ecs.put("host", host);
            }
        }

        return ecs;
    }

    @Override
    public Set<String> supportedSources() {
        return Set.of("SIEM");
    }
}
