package com.cybersec.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 告警事件 Kafka 生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${cybersec.kafka.topics.alert-events:cybersec.alert.events}")
    private String topic;

    public void sendAlertEvent(String key, String alertJson) {
        try {
            kafkaTemplate.send(topic, key, alertJson).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send alert event: key={}, error={}", key, ex.getMessage());
                } else {
                    log.debug("Alert event sent: key={}, offset={}", key,
                            result != null ? result.getRecordMetadata().offset() : "unknown");
                }
            });
        } catch (Exception e) {
            log.error("Kafka send error: key={}, error={}", key, e.getMessage());
        }
    }

    public void sendAlertEvent(String alertJson) {
        // 使用时间戳作为默认key
        sendAlertEvent(String.valueOf(System.currentTimeMillis()), alertJson);
    }
}
