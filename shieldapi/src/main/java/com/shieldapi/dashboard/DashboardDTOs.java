package com.shieldapi.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * All DTOs for the SOC Dashboard API — co-located in the dashboard package.
 */
public class DashboardDTOs {

    // ─── /api/dashboard/metrics ─────────────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MetricsDTO {
        private long   activeThreats;
        private long   requestsPerMin;
        private long   blockedToday;
        private int    riskScore;
        private int    avgLatencyMs;      // NEW: Average system latency
        private String topTargetEndpoint;  // NEW: Most attacked API route
    }

    // ─── /api/dashboard/attacks ─────────────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AttackDTO {
        private String        type;
        private String        ip;
        private String        endpoint;
        private String        severity;
        private LocalDateTime timestamp;
        private String        cc;       // Country Code (e.g. "US", "RU")
        private String        asn;      // Provider/ISP (e.g. "DigitalOcean")
    }

    // ─── /api/dashboard/top-offenders ───────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class OffenderDTO {
        private String ip;
        private long   requests;
        private String status;   // BLOCKED | MONITOR | ALLOW
        private String cc;
        private String asn;
    }

    // ─── /api/dashboard/traffic ─────────────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TrafficPointDTO {
        private String h;        // e.g. "00:00"
        private long   trf;      // total requests
        private long   atk;      // attack events
        private long   blk;      // blocked requests
    }
}
