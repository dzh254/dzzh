package com.cybersec.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka 配置
 */
@Slf4j
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${cybersec.kafka.topics.alert-events:cybersec.alert.events}")
    private String alertEventsTopic;

    @Bean
    public NewTopic alertEventsTopic() {
        return TopicBuilder.name(alertEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
