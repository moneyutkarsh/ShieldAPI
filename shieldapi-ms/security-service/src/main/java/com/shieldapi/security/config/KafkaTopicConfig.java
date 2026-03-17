package com.shieldapi.security.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic securityEventsTopic() {
        return TopicBuilder.name("security-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic systemMetricsTopic() {
        return TopicBuilder.name("system-metrics")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic alertEventsTopic() {
        return TopicBuilder.name("alert-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
