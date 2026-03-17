package com.shieldapi.security.service;

import com.shieldapi.common.model.ThreatCategory;
import com.shieldapi.security.model.ThreatScore;
import com.shieldapi.security.repository.ThreatScoreRepository;
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
            case BRUTE_FORCE -> 25;
            case RATE_LIMIT_ABUSE -> 10;
            case SUSPICIOUS_ACCESS -> 40;
            case HONEYPOT_TRAP -> 100;
            case RECON_ATTACK, RECON_SCAN -> 50;
            case SQL_INJECTION, XSS_PAYLOAD -> 75;
            default -> 0;
        };
    }

    private void triggerMitigation(String ipAddress, int score) {
        log.warn("Mitigation Triggered! IP: {} exceeded threat score threshold", ipAddress);
        ipReputationService.blacklistIp(ipAddress, "Automatic mitigation: Score " + score, score);
    }
}
