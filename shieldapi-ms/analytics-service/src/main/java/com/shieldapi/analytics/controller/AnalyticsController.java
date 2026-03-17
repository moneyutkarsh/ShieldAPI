package com.shieldapi.analytics.controller;

import com.shieldapi.analytics.service.AnalyticsService;
import com.shieldapi.common.dto.DashboardDTOs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/health")
    public String health() {
        return "UP";
    }

    @GetMapping("/stats")
    public DashboardDTOs.MetricsDTO getStats() {
        return analyticsService.getMetrics();
    }

    @GetMapping("/logs")
    public List<DashboardDTOs.AttackDTO> getLogs() {
        return analyticsService.getRecentAttacks();
    }

    @GetMapping("/threats")
    public List<Map<String, Object>> getThreatsAggregation() {
        return analyticsService.getThreatsAggregation();
    }
}
