package com.shieldapi.security.threatscore;

import com.shieldapi.security.ipreputation.IpReputationService;
import com.shieldapi.security.threatintel.ThreatCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreatScoreService {

    private final ThreatScoreRepository threatScoreRepository;
    private final IpReputationService ipReputationService;

    @Value("${shieldapi.security.threatscore.threshold:100}")
    private int mitigationThreshold;

    @Value("${shieldapi.security.threatscore.weights.auth-failure:25}")
    private int authFailureWeight;

    @Value("${shieldapi.security.threatscore.weights.rate-limit:10}")
    private int rateLimitWeight;

    @Value("${shieldapi.security.threatscore.weights.suspicious-access:40}")
    private int suspiciousAccessWeight;

    @Transactional
    public void updateScore(String ipAddress, ThreatCategory category) {
        int weight = getWeightForCategory(category);
        
        ThreatScore score = threatScoreRepository.findByIpAddress(ipAddress)
                .orElse(ThreatScore.builder()
                        .ipAddress(ipAddress)
                        .currentScore(0)
                        .lastUpdated(LocalDateTime.now())
                        .build());

        score.setCurrentScore(score.getCurrentScore() + weight);
        score.setLastUpdated(LocalDateTime.now());
        threatScoreRepository.save(score);

        log.info("Updated Threat Score for IP: {}. New Score: {}", ipAddress, score.getCurrentScore());

        if (score.getCurrentScore() >= mitigationThreshold) {
            triggerMitigation(ipAddress, score.getCurrentScore());
        }
    }

    private int getWeightForCategory(ThreatCategory category) {
        return switch (category) {
            case BRUTE_FORCE -> authFailureWeight;
            case RATE_LIMIT_ABUSE -> rateLimitWeight;
            case SUSPICIOUS_ACCESS -> suspiciousAccessWeight;
            case HONEYPOT_TRAP -> 100; // Direct hit on deception endpoint
            case RECON_ATTACK, RECON_SCAN -> 50; // Behavioral detection
            case SQL_INJECTION -> 75;
            case XSS_PAYLOAD -> 75;
        };
    }

    private void triggerMitigation(String ipAddress, int score) {
        log.warn("Mitigation Triggered! IP: {} exceeded threat score threshold with score: {}", ipAddress, score);
        
        // Use IpReputationService to blacklist the IP
        ipReputationService.blacklistIp(
                ipAddress, 
                "Automatic mitigation: Threat score " + score + " exceeded threshold", 
                score
        );
        
        log.info("IP: {} has been automatically blacklisted.", ipAddress);
    }
}
