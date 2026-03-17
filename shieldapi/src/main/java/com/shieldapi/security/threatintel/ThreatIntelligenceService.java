package com.shieldapi.security.threatintel;

import com.shieldapi.security.threatscore.ThreatScoreService;
import com.shieldapi.monitoring.metrics.SecurityMetricsService;
import com.shieldapi.security.events.SecurityEvent;
import com.shieldapi.security.events.SecurityEventPublisher;
import com.shieldapi.dto.ThreatEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreatIntelligenceService {

    private final ThreatEventRepository threatEventRepository;
    private final SecurityMetricsService securityMetricsService;
    private final ThreatScoreService threatScoreService;
    private final SecurityEventPublisher securityEventPublisher;

    public List<ThreatEventDTO> getAllThreatEvents() {
        return threatEventRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ThreatEventDTO> getThreatEventsByIp(String ip) {
        return threatEventRepository.findAll().stream()
                .filter(event -> event.getIpAddress().equals(ip))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ThreatEventDTO mapToDTO(ThreatEvent event) {
        return ThreatEventDTO.builder()
                .id(event.getId())
                .ipAddress(event.getIpAddress())
                .endpoint(event.getEndpoint())
                .threatCategory(event.getThreatCategory())
                .severity(event.getSeverity())
                .attemptCount(event.getAttemptCount())
                .detectedAt(event.getDetectedAt())
                .build();
    }

    @Transactional
    public void recordThreat(String ipAddress, String endpoint, ThreatCategory category) {
        log.warn("Security Threat Detected! IP: {}, Category: {}, Endpoint: {}", ipAddress, category, endpoint);
        securityMetricsService.incrementThreatsDetected();
        threatScoreService.updateScore(ipAddress, category);

        // Publish Security Event to Kafka
        securityEventPublisher.publish(SecurityEvent.builder()
                .eventType("THREAT_DETECTED")
                .sourceIp(ipAddress)
                .endpoint(endpoint)
                .severity("MEDIUM") // Initial severity, recalculated below
                .details(Map.of(
                        "category", category.name(),
                        "action", "THREAT_RECORDED"
                ))
                .build());

        ThreatEvent lastEvent = threatEventRepository
                .findTopByIpAddressAndThreatCategoryOrderByDetectedAtDesc(ipAddress, category)
                .orElse(null);

        // If a similar event happened in the last 10 minutes, increment counter
        if (lastEvent != null && lastEvent.getDetectedAt().isAfter(LocalDateTime.now().minusMinutes(10))) {
            lastEvent.setAttemptCount(lastEvent.getAttemptCount() + 1);
            lastEvent.setSeverity(calculateSeverity(category, lastEvent.getAttemptCount()));
            lastEvent.setEndpoint(endpoint); // Update to latest endpoint attempt
            threatEventRepository.save(lastEvent);
        } else {
            ThreatEvent newEvent = ThreatEvent.builder()
                    .ipAddress(ipAddress)
                    .endpoint(endpoint)
                    .threatCategory(category)
                    .attemptCount(1)
                    .severity(calculateSeverity(category, 1))
                    .build();
            threatEventRepository.save(newEvent);
        }
    }

    private Severity calculateSeverity(ThreatCategory category, int count) {
        if (count > 50) return Severity.CRITICAL;
        if (count > 20) return Severity.HIGH;
        if (count > 5) return Severity.MEDIUM;
        return Severity.LOW;
    }
}
