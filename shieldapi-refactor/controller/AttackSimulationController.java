package com.shieldapi.controller;

import com.shieldapi.security.threatintel.Severity;
import com.shieldapi.service.AttackSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/simulate")
@RequiredArgsConstructor
public class AttackSimulationController {

    private final AttackSimulationService simulationService;

    @GetMapping("/sql")
    public Map<String, String> simulateSqlInjection() {
        return simulationService.simulateAttack("SQL_INJECTION", "185.220.101.47", "/api/login", Severity.CRITICAL);
    }

    @GetMapping("/xss")
    public Map<String, String> simulateXss() {
        return simulationService.simulateAttack("XSS_PAYLOAD", "45.130.228.91", "/api/search", Severity.HIGH);
    }

    @GetMapping("/bruteforce")
    public Map<String, String> simulateBruteForce() {
        return simulationService.simulateAttack("BRUTE_FORCE", "91.108.4.67", "/api/login", Severity.MEDIUM);
    }

    @GetMapping("/recon")
    public Map<String, String> simulateRecon() {
        return simulationService.simulateAttack("RECON_SCAN", "162.55.12.44", "/api/internal", Severity.LOW);
    }
}
