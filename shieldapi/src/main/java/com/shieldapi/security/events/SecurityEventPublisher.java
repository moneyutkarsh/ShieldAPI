package com.shieldapi.security.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityEventPublisher {

    private final KafkaTemplate<String, SecurityEvent> kafkaTemplate;

    @Value("${shieldapi.security.events.topic}")
    private String topic;

    public void publish(SecurityEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }

        log.info("Publishing security event to Kafka [Topic: {}]: {}", topic, event.getEventType());
        
        try {
            kafkaTemplate.send(topic, event.getEventId(), event);
        } catch (Exception e) {
            log.error("Failed to publish security event to Kafka: {}", e.getMessage(), e);
        }
    }
}
