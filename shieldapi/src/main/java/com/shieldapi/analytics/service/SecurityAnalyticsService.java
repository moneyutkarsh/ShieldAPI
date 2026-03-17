package com.shieldapi.analytics.service;

import com.shieldapi.analytics.dto.*;
import com.shieldapi.dto.ThreatEventDTO;
import com.shieldapi.monitoring.audit.ApiAuditLog;
import com.shieldapi.monitoring.audit.ApiAuditLogRepository;
import com.shieldapi.security.threatintel.ThreatCategory;
import com.shieldapi.security.threatintel.ThreatEvent;
import com.shieldapi.security.threatintel.ThreatEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecurityAnalyticsService {

    private final ThreatEventRepository threatEventRepository;
    private final ApiAuditLogRepository apiAuditLogRepository;
    private final com.shieldapi.security.threatscore.ThreatScoreRepository threatScoreRepository;

    public Map<String, Object> getSecurityMetrics() {
        Map<String, Object> metrics = new java.util.HashMap<>();
        
        // 1. Total Attacks (last 24h)
        metrics.put("totalAttacks24h", threatEventRepository.count()); 
        
        // 2. Threat Score Distribution
        metrics.put("threatScoreDistribution", getThreatScoreDistribution());
        
        // 3. Top Attacking IPs
        metrics.put("topAttackingIps", getTopAttackers().stream().limit(5).collect(Collectors.toList()));
        
        // 4. Category Breakdown
        metrics.put("categoryBreakdown", getCategoryBreakdown());

        return metrics;
    }

    private Map<String, Long> getThreatScoreDistribution() {
        Map<String, Long> distribution = new java.util.HashMap<>();
        long high = threatScoreRepository.countByCurrentScoreGreaterThanEqual(80);
        long medium = threatScoreRepository.countByCurrentScoreBetween(40, 79);
        long low = threatScoreRepository.countByCurrentScoreLessThan(40);
        
        distribution.put("HIGH", high);
        distribution.put("MEDIUM", medium);
        distribution.put("LOW", low);
        return distribution;
    }

    private Map<ThreatCategory, Long> getCategoryBreakdown() {
        return threatEventRepository.findAll().stream()
                .collect(Collectors.groupingBy(event -> event.getThreatCategory(), Collectors.counting()));
    }

    public ThreatSummaryDTO getThreatSummary() {
        List<ThreatEvent> allEvents = threatEventRepository.findAll();
        
        Map<String, Long> byCategory = allEvents.stream()
                .collect(Collectors.groupingBy(e -> e.getThreatCategory().name(), Collectors.summingLong(ThreatEvent::getAttemptCount)));
        
        Map<String, Long> bySeverity = allEvents.stream()
                .collect(Collectors.groupingBy(e -> e.getSeverity().name(), Collectors.summingLong(ThreatEvent::getAttemptCount)));

        long uniqueIps = allEvents.stream()
                .map(ThreatEvent::getIpAddress)
                .distinct()
                .count();

        return ThreatSummaryDTO.builder()
                .totalThreats(allEvents.stream().mapToLong(ThreatEvent::getAttemptCount).sum())
                .threatsByCategory(byCategory)
                .threatsBySeverity(bySeverity)
                .uniqueAttackingIps(uniqueIps)
                .build();
    }

    public List<TopAttackerDTO> getTopAttackers() {
        return threatEventRepository.findTopAttackers();
    }

    public List<ThreatEventDTO> getRateLimitViolations() {
        return threatEventRepository.findAll().stream()
                .filter(e -> e.getThreatCategory() == ThreatCategory.RATE_LIMIT_ABUSE)
                .sorted((t1, t2) -> t2.getDetectedAt().compareTo(t1.getDetectedAt()))
                .limit(100)
                .map(this::mapToThreatDTO)
                .collect(Collectors.toList());
    }

    public List<SecurityAuditLogDTO> getRecentAuditLogs() {
        return apiAuditLogRepository.findAll(PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "timestamp")))
                .getContent().stream()
                .map(this::mapToAuditDTO)
                .collect(Collectors.toList());
    }

    private ThreatEventDTO mapToThreatDTO(ThreatEvent event) {
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

    private SecurityAuditLogDTO mapToAuditDTO(ApiAuditLog log) {
        return SecurityAuditLogDTO.builder()
                .id(log.getId())
                .ipAddress(log.getIpAddress())
                .requestUri(log.getRequestUri())
                .httpMethod(log.getHttpMethod())
                .responseStatus(log.getResponseStatus())
                .responseTime(log.getResponseTime())
                .timestamp(log.getTimestamp())
                .build();
    }
}
