package com.shieldapi.gateway.service;

import com.shieldapi.common.dto.SecurityEventDTO;
import com.shieldapi.common.model.Severity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewaySecurityEventConsumer {

    private final DynamicBlacklistService blacklistService;

    @KafkaListener(topics = "security-events", groupId = "gateway-event-group")
    public void handleSecurityEvent(SecurityEventDTO event) {
        log.info("Gateway received security event: {} from IP: {}", event.getCategory(), event.getIpAddress());

        // Auto-blacklist logic:
        // 1. Any Critical threat is blocked for 60 minutes
        // 2. High threats with more than 5 attempts are blocked for 30 minutes
        // 3. Repeated Medium threats (e.g. Rate Limit) handled by the filter itself, but we can sync here too
        
        if (event.getSeverity() == Severity.CRITICAL) {
            blacklistService.blacklistIp(event.getIpAddress(), 60);
        } else if (event.getSeverity() == Severity.HIGH && event.getAttemptCount() >= 5) {
            blacklistService.blacklistIp(event.getIpAddress(), 15);
        }
    }
}
