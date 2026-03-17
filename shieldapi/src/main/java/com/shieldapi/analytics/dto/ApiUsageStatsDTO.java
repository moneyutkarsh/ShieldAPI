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
public class ApiUsageStatsDTO {
    private long totalRequests;
    private double averageResponseTime;
    private Map<Integer, Long> statusCodeDistribution;
}
