package com.cybersec.domain.service.impl;

import com.cybersec.domain.service.AlertNormalizer;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * EDR 告警标准化器
 */
@Component
public class EdrAlertNormalizer implements AlertNormalizer {

    @Override
    public Map<String, Object> normalize(Map<String, Object> rawAlert, String sourceType) {
        Map<String, Object> ecs = new LinkedHashMap<>();

        // event 对象
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("category", List.of("malware"));
        event.put("type", List.of("alert"));
        event.put("module", "edr");
        if (rawAlert.containsKey("action")) {
            event.put("action", rawAlert.get("action"));
        }
        event.put("original", rawAlert);
        ecs.put("event", event);

        // host 对象
        if (rawAlert.containsKey("hostname")) {
            Map<String, Object> host = new LinkedHashMap<>();
            host.put("name", rawAlert.get("hostname"));
            ecs.put("host", host);
        }

        // process 对象
        Map<String, Object> process = new LinkedHashMap<>();
        if (rawAlert.containsKey("process_name")) {
            process.put("name", rawAlert.get("process_name"));
        }
        if (rawAlert.containsKey("process_id")) {
            process.put("pid", rawAlert.get("process_id"));
        }
        if (rawAlert.containsKey("command_line")) {
            process.put("command_line", rawAlert.get("command_line"));
        }
        // parent process
        if (rawAlert.containsKey("parent_process")) {
            Map<String, Object> parent = new LinkedHashMap<>();
            parent.put("name", rawAlert.get("parent_process"));
            process.put("parent", parent);
        }
        if (!process.isEmpty()) {
            ecs.put("process", process);
        }

        // file 对象
        if (rawAlert.containsKey("file_hash")) {
            Map<String, Object> file = new LinkedHashMap<>();
            Map<String, Object> hash = new LinkedHashMap<>();
            hash.put("sha256", rawAlert.get("file_hash"));
            file.put("hash", hash);
            ecs.put("file", file);
        }

        return ecs;
    }

    @Override
    public Set<String> supportedSources() {
        return Set.of("EDR");
    }
}
