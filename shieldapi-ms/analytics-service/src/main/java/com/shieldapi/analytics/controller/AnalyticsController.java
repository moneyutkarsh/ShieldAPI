package com.shieldapi.analytics.controller;

import com.shieldapi.analytics.service.AnalyticsService;
import com.shieldapi.common.dto.DashboardDTOs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/metrics")
    public DashboardDTOs.MetricsDTO getMetrics() {
        return analyticsService.getMetrics();
    }

    @GetMapping("/attacks")
    public List<DashboardDTOs.AttackDTO> getRecentAttacks() {
        return analyticsService.getRecentAttacks();
    }
}
