package com.shieldapi.notification.service;

import com.shieldapi.common.dto.AlertEventDTO;
import com.shieldapi.common.model.Severity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertPublisher {

    private static final Logger log = LoggerFactory.getLogger(AlertPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "alert-events";

    public void publishAlert(String title, String message, Severity severity, String source) {
        AlertEventDTO alert = AlertEventDTO.builder()
                .alertId(UUID.randomUUID().toString())
                .title(title)
                .message(message)
                .severity(severity)
                .source(source)
                .timestamp(LocalDateTime.now())
                .build();
        
        kafkaTemplate.send(TOPIC, alert.getAlertId(), alert);
        log.info("System alert published: [{}] {}", severity, title);
    }
}
