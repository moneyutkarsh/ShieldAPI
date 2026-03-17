package com.shieldapi.analytics.service;

import com.shieldapi.analytics.repository.ThreatMetricRepository;
import com.shieldapi.common.dto.DashboardDTOs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ThreatMetricRepository threatMetricRepository;

    @org.springframework.cache.annotation.Cacheable(value = "dashboard-metrics")
    public DashboardDTOs.MetricsDTO getMetrics() {
        long totalThreats = threatMetricRepository.count();
        // Since we are decoupling, some metrics like RPM might need a local cache or influxdb, 
        // for now we aggregate from H2 for simplicity.
        return DashboardDTOs.MetricsDTO.builder()
                .activeThreats(totalThreats)
                .requestsPerMin(0) // Logic to be implemented or fetched from Edge
                .riskScore((int) Math.min(99, totalThreats * 2))
                .avgLatencyMs(45)
                .topTargetEndpoint("api/v1/auth/login")
                .build();
    }

    @org.springframework.cache.annotation.Cacheable(value = "recent-attacks")
    public List<DashboardDTOs.AttackDTO> getRecentAttacks() {
        return threatMetricRepository.findAll().stream()
                .sorted(Comparator.comparing(m -> m.getTimestamp(), Comparator.reverseOrder()))
                .limit(20)
                .map(m -> DashboardDTOs.AttackDTO.builder()
                        .ip(m.getIpAddress())
                        .type(m.getCategory())
                        .severity(m.getSeverity())
                        .timestamp(m.getTimestamp())
                        .endpoint(m.getEndpoint())
                        .cc("US")
                        .asn("Datacenter")
                        .build())
                .collect(Collectors.toList());
    }
}
