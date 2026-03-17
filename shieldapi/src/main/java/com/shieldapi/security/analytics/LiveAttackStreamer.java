package com.shieldapi.security.analytics;

import com.shieldapi.security.events.SecurityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveAttackStreamer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "${shieldapi.security.events.topic}", groupId = "shieldapi-soc-group")
    public void handleSecurityEvent(SecurityEvent event) {
        log.debug("Streaming security event to WebSocket: {}", event.getEventType());
        messagingTemplate.convertAndSend("/topic/security-alerts", event);
    }
}
