package com.shieldapi.security.ipreputation;

import com.shieldapi.security.events.SecurityEvent;
import com.shieldapi.security.events.SecurityEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IpReputationService {

    private final BlockedIpRepository blockedIpRepository;
    private final SecurityEventPublisher securityEventPublisher;

    public boolean isIpBlocked(String ipAddress) {
        return blockedIpRepository.findByIpAddress(ipAddress)
                .map(blockedIp -> {
                    // Check if the block has expired
                    if (blockedIp.getExpiresAt() != null && blockedIp.getExpiresAt().isBefore(LocalDateTime.now())) {
                        log.info("IP block for {} has expired. Unblocking...", ipAddress);
                        blockedIpRepository.delete(blockedIp);
                        return false;
                    }
                    return true;
                })
                .orElse(false);
    }

    public void blacklistIp(String ipAddress, String reason, int threatScore) {
        log.warn("Blacklisting IP: {}. Reason: {}", ipAddress, reason);
        
        BlockedIpAddress block = BlockedIpAddress.builder()
                .ipAddress(ipAddress)
                .reason(reason)
                .threatScore(threatScore)
                .blockedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7)) // Default 7-day block
                .build();
                
        blockedIpRepository.save(block);

        // Publish Security Event to Kafka
        securityEventPublisher.publish(com.shieldapi.security.events.SecurityEvent.builder()
                .eventType("IP_BLACKLISTED")
                .sourceIp(ipAddress)
                .severity("HIGH")
                .details(Map.of(
                        "action", "IP_BLOCKED",
                        "reason", reason,
                        "threatScore", threatScore,
                        "expiresAt", block.getExpiresAt().toString()
                ))
                .build());
    }
}
