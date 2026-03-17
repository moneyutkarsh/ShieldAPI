package com.shieldapi.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatSummaryDTO {
    private long totalThreats;
    private Map<String, Long> threatsByCategory;
    private Map<String, Long> threatsBySeverity;
    private long uniqueAttackingIps;
}
