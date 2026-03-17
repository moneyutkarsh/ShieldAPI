package com.shieldapi.analytics.controller;

import com.shieldapi.analytics.dto.SecurityAuditLogDTO;
import com.shieldapi.analytics.dto.ThreatSummaryDTO;
import com.shieldapi.analytics.dto.TopAttackerDTO;
import com.shieldapi.analytics.service.SecurityAnalyticsService;
import com.shieldapi.dto.ThreatEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/security")
@RequiredArgsConstructor
public class SecurityAnalyticsController {

    private final SecurityAnalyticsService securityAnalyticsService;

    @GetMapping("/threat-summary")
    public ResponseEntity<ThreatSummaryDTO> getThreatSummary() {
        return ResponseEntity.ok(securityAnalyticsService.getThreatSummary());
    }

    @GetMapping("/top-attacking-ips")
    public ResponseEntity<List<TopAttackerDTO>> getTopAttackingIps() {
        return ResponseEntity.ok(securityAnalyticsService.getTopAttackers());
    }

    @GetMapping("/rate-limit-violations")
    public ResponseEntity<List<ThreatEventDTO>> getRateLimitViolations() {
        return ResponseEntity.ok(securityAnalyticsService.getRateLimitViolations());
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<SecurityAuditLogDTO>> getAuditLogs() {
        return ResponseEntity.ok(securityAnalyticsService.getRecentAuditLogs());
    }
}
