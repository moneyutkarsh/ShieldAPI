package com.shieldapi.security.controller;

import com.shieldapi.common.model.ThreatCategory;
import com.shieldapi.security.service.IpReputationService;
import com.shieldapi.security.service.ThreatDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityController {

    private final ThreatDetectionService threatDetectionService;
    private final IpReputationService ipReputationService;

    @PostMapping("/detect")
    public void detect(@RequestParam String ip, @RequestParam String endpoint, @RequestParam ThreatCategory category) {
        threatDetectionService.processThreat(ip, endpoint, category);
    }

    @GetMapping("/check-ip")
    public Map<String, Boolean> checkIp(@RequestParam String ip) {
        return Map.of("blacklisted", ipReputationService.isIpBlacklisted(ip));
    }

    @GetMapping("/simulate/sql")
    public Map<String, String> simulateSql() {
        threatDetectionService.processThreat("185.220.101.47", "/api/login", ThreatCategory.SQL_INJECTION);
        return Map.of("status", "Simulated SQL Injection from TOR Exit Node");
    }

    @GetMapping("/simulate/xss")
    public Map<String, String> simulateXss() {
        threatDetectionService.processThreat("45.130.228.91", "/api/search", ThreatCategory.XSS_PAYLOAD);
        return Map.of("status", "Simulated XSS Payload from Dutch VPS");
    }

    @GetMapping("/simulate/bruteforce")
    public Map<String, String> simulateBruteForce() {
        threatDetectionService.processThreat("91.108.4.67", "/api/login", ThreatCategory.BRUTE_FORCE);
        return Map.of("status", "Simulated Brute Force attack detected");
    }

    @GetMapping("/simulate/recon")
    public Map<String, String> simulateRecon() {
        threatDetectionService.processThreat("162.55.12.44", "/api/internal", ThreatCategory.RECON_SCAN);
        return Map.of("status", "Simulated Reconnaissance Scan from German Cloud");
    }
}
