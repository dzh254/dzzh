package com.cybersec.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 告警事件 Kafka 消费者 — 异步处理告警标准化
 */
@Slf4j
@Component
public class AlertEventConsumer {

    @KafkaListener(
            topics = "${cybersec.kafka.topics.alert-events:cybersec.alert.events}",
            groupId = "cybersec-alert-processor"
    )
    public void consume(String message) {
        log.debug("Received alert event: length={}", message != null ? message.length() : 0);
        // TODO: Stage 2 — trigger async normalization + enrichment pipeline
        // The message is a JSON-serialized alert that needs:
        // 1. Parse and validate
        // 2. Enrich with threat intel
        // 3. Enrich with asset context
        // 4. Trigger real-time analysis if severity >= HIGH
    }
}
