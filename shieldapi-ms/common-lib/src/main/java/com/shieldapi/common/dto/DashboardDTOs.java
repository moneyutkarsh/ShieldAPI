package com.shieldapi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricsDTO {
        private long activeThreats;
        private long requestsPerMin;
        private long blockedToday;
        private int riskScore;
        private int avgLatencyMs;
        private String topTargetEndpoint;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttackDTO {
        private String type;
        private String ip;
        private String endpoint;
        private String severity;
        private LocalDateTime timestamp;
        private String cc;
        private String asn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OffenderDTO {
        private String ip;
        private long requests;
        private String status;
        private String cc;
        private String asn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrafficPointDTO {
        private String h;
        private long trf;
        private long atk;
        private long blk;
    }
}
