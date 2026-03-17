package com.shieldapi.security.recon;

import com.shieldapi.security.events.SecurityEventPublisher;
import com.shieldapi.security.threatintel.ThreatCategory;
import com.shieldapi.security.threatscore.ThreatScoreService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconDetectionService {

    private final ThreatScoreService threatScoreService;
    private final SecurityEventPublisher securityEventPublisher;

    private final Map<String, RequestStats> ipStatsMap = new ConcurrentHashMap<>();

    @Value("${shieldapi.security.recon.scan-threshold:10}")
    private int scanThreshold;

    @Value("${shieldapi.security.recon.detection-window-seconds:60}")
    private int windowSeconds;

    @Value("${shieldapi.security.recon.failure-threshold:5}")
    private int failureThreshold;

    public void processRequest(String ipAddress, String endpoint, int statusCode) {
        RequestStats stats = ipStatsMap.computeIfAbsent(ipAddress, k -> new RequestStats());
        
        LocalDateTime now = LocalDateTime.now();
        stats.cleanup(now.minusSeconds(windowSeconds));
        
        stats.addRequest(endpoint, statusCode, now);
        
        checkReconPattern(ipAddress, stats);
    }

    private void checkReconPattern(String ipAddress, RequestStats stats) {
        if (stats.getUniqueEndpointsCount() > scanThreshold) {
            triggerReconAlert(ipAddress, "HIGH_ENDPOINT_DIVERSITY", 
                "Accessed " + stats.getUniqueEndpointsCount() + " unique endpoints");
            stats.resetCounters();
        } else if (stats.getFailureCount() > failureThreshold) {
            triggerReconAlert(ipAddress, "REPEATED_AUTH_FAILURES", 
                "Detected " + stats.getFailureCount() + " unauthorized attempts");
            stats.resetCounters();
        }
    }

    private void triggerReconAlert(String ipAddress, String reason, String details) {
        log.warn("Reconnaissance detected! IP: {}, Reason: {}, Details: {}", ipAddress, reason, details);

        // 1. Update Threat Score
        threatScoreService.updateScore(ipAddress, ThreatCategory.RECON_ATTACK);

        // 2. Publish Kafka Event
        securityEventPublisher.publish(com.shieldapi.security.events.SecurityEvent.builder()
                .eventType("RECON_ATTACK")
                .sourceIp(ipAddress)
                .severity("HIGH")
                .details(Map.of(
                        "reason", reason,
                        "details", details,
                        "timestamp", LocalDateTime.now().toString()
                ))
                .build());
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void cleanupStats() {
        LocalDateTime expiry = LocalDateTime.now().minusSeconds(windowSeconds);
        ipStatsMap.entrySet().removeIf(entry -> {
            entry.getValue().cleanup(expiry);
            return entry.getValue().isEmpty();
        });
    }

    @Data
    private static class RequestStats {
        private final Set<String> uniqueEndpoints = Collections.newSetFromMap(new ConcurrentHashMap<>());
        private final List<RequestRecord> history = Collections.synchronizedList(new ArrayList<>());
        private int failureCount = 0;

        public void addRequest(String endpoint, int statusCode, LocalDateTime timestamp) {
            uniqueEndpoints.add(endpoint);
            history.add(new RequestRecord(endpoint, statusCode, timestamp));
            if (statusCode == 401 || statusCode == 403) {
                failureCount++;
            }
        }

        public void cleanup(LocalDateTime expiry) {
            history.removeIf(record -> {
                if (record.getTimestamp().isBefore(expiry)) {
                    if (record.getStatusCode() == 401 || record.getStatusCode() == 403) {
                        failureCount--;
                    }
                    return true;
                }
                return false;
            });
            
            // Re-sync unique endpoints from remaining history
            uniqueEndpoints.clear();
            history.forEach(r -> uniqueEndpoints.add(r.getEndpoint()));
        }

        public int getUniqueEndpointsCount() {
            return uniqueEndpoints.size();
        }

        public boolean isEmpty() {
            return history.isEmpty();
        }

        public void resetCounters() {
            uniqueEndpoints.clear();
            history.clear();
            failureCount = 0;
        }
    }

    @Data
    @AllArgsConstructor
    private static class RequestRecord {
        private String endpoint;
        private int statusCode;
        private LocalDateTime timestamp;
    }
}
