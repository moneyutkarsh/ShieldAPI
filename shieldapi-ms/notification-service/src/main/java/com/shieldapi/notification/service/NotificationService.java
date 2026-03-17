package com.shieldapi.notification.service;

import com.shieldapi.common.dto.SecurityEventDTO;
import com.shieldapi.common.model.Severity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final AlertPublisher alertPublisher;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 2000, multiplier = 2.0),
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "security-events", groupId = "notification-group")
    public void handleSecurityEvent(SecurityEventDTO event) {
        log.info("Streaming security alert to Dashboard: {}", event.getCategory());
        
        // Match the expected topic for the React frontend
        messagingTemplate.convertAndSend("/topic/security-alerts", event);
        
        // Publish to alert-events topic for other services to consume if needed
        alertPublisher.publishAlert(
                "Security Threat: " + event.getCategory(),
                event.getMessage(),
                event.getSeverity(),
                "security-service"
        );

        // If critical, we could also send email/slack here
        if ("CRITICAL".equals(event.getSeverity().name())) {
            log.error("CRITICAL THREAT DETECTED: Sending emergency notifications!");
        }
    }

    @DltHandler
    public void handleDlt(SecurityEventDTO event) {
        log.error("Notification Event sent to DLQ: {}", event.getEventId());
    }
}
