package com.shieldapi.security.service;

import com.shieldapi.common.dto.SecurityEventDTO;
import com.shieldapi.common.model.Severity;
import com.shieldapi.common.model.ThreatCategory;
import io.micrometer.core.instrument.MeterRegistry;
import com.shieldapi.security.model.ThreatEvent;
import com.shieldapi.security.repository.ThreatEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreatDetectionService {

    private final ThreatEventRepository threatEventRepository;
    private final ThreatScoreService threatScoreService;
    private final KafkaTemplate<String, SecurityEventDTO> kafkaTemplate;
    private final MeterRegistry meterRegistry;
    
    private static final String TOPIC = "security-events";

    @Transactional
    public void processThreat(String ip, String endpoint, ThreatCategory category) {
        log.warn("Security Threat Detected! IP: {}, Category: {}, Endpoint: {}", ip, category, endpoint);
        meterRegistry.counter("shieldapi.threats.detected", "category", category.name()).increment();
        
        // 1. Update internal threat score
        threatScoreService.updateScore(ip, category);

        // 2. Persist or Update threat event
        ThreatEvent event = threatEventRepository.findAll().stream()
                .filter(e -> e.getIpAddress().equals(ip) && e.getThreatCategory() == category)
                .filter(e -> e.getDetectedAt().isAfter(LocalDateTime.now().minusMinutes(10)))
                .findFirst()
                .orElse(null);

        if (event != null) {
            event.setAttemptCount(event.getAttemptCount() + 1);
            event.setSeverity(calculateSeverity(category, event.getAttemptCount()));
            event.setEndpoint(endpoint);
            threatEventRepository.save(event);
        } else {
            event = ThreatEvent.builder()
                    .ipAddress(ip)
                    .endpoint(endpoint)
                    .threatCategory(category)
                    .attemptCount(1)
                    .severity(calculateSeverity(category, 1))
                    .detectedAt(LocalDateTime.now())
                    .build();
            threatEventRepository.save(event);
        }

        // 3. Publish to Kafka for Analytics and Notifications
        publishSecurityEvent(event);
    }

    private void publishSecurityEvent(ThreatEvent event) {
        SecurityEventDTO dto = SecurityEventDTO.builder()
                .eventId(UUID.randomUUID().toString())
                .ipAddress(event.getIpAddress())
                .endpoint(event.getEndpoint())
                .category(event.getThreatCategory())
                .severity(event.getSeverity())
                .message("Threat detected: " + event.getThreatCategory())
                .timestamp(event.getDetectedAt())
                .attemptCount(event.getAttemptCount())
                .build();

        kafkaTemplate.send(TOPIC, dto.getIpAddress(), dto);
        log.info("Security event published to Kafka for IP: {}", dto.getIpAddress());
    }

    private Severity calculateSeverity(ThreatCategory category, int count) {
        if (count > 50) return Severity.CRITICAL;
        if (count > 20) return Severity.HIGH;
        if (count > 5) return Severity.MEDIUM;
        return Severity.LOW;
    }
}
