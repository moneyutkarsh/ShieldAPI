package com.shieldapi.security.honeypot;

import com.shieldapi.security.events.SecurityEventPublisher;
import com.shieldapi.security.ipreputation.IpReputationService;
import com.shieldapi.security.threatintel.ThreatCategory;
import com.shieldapi.security.threatscore.ThreatScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HoneypotService {

    private final ThreatScoreService threatScoreService;
    private final SecurityEventPublisher securityEventPublisher;
    private final IpReputationService ipReputationService;

    @Value("${shieldapi.security.honeypot.paths:/admin-panel,/phpmyadmin,/.env,/.git,/wp-login}")
    private List<String> honeypotPaths;

    @Value("${shieldapi.security.honeypot.blacklist-on-hit:true}")
    private boolean blacklistOnHit;

    public boolean isHoneypotPath(String path) {
        return honeypotPaths.stream().anyMatch(path::equalsIgnoreCase);
    }

    public void handleHoneypotHit(String ipAddress, String path, String method) {
        log.warn("Honeypot hit detected! IP: {}, Path: {}, Method: {}", ipAddress, path, method);

        // 1. Update threat score
        threatScoreService.updateScore(ipAddress, ThreatCategory.HONEYPOT_TRAP);

        // 2. Publish security event
        securityEventPublisher.publish(com.shieldapi.security.events.SecurityEvent.builder()
                .eventType("HONEYPOT_HIT")
                .sourceIp(ipAddress)
                .severity("CRITICAL")
                .details(Map.of(
                        "path", path,
                        "method", method,
                        "action", blacklistOnHit ? "IP_BLACKLISTED" : "THREAT_SCORE_INCREASED"
                ))
                .build());

        // 3. Blacklist IP if configured
        if (blacklistOnHit) {
            ipReputationService.blacklistIp(
                    ipAddress,
                    "Honeypot trap hit: " + path,
                    100 // High impact
            );
        }
    }
}
