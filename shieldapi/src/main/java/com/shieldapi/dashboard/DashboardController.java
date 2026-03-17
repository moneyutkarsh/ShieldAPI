package com.shieldapi.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public SOC Dashboard REST API.
 *
 * All four endpoints are permit-all in SecurityConfig — no JWT required
 * so the React dashboard can poll without authentication.
 *
 * CORS is handled globally in CorsConfig; @CrossOrigin here is a safety net.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard/metrics
     * Returns KPI summary: activeThreats, requestsPerMin, blockedToday, riskScore
     */
    @GetMapping("/metrics")
    public ResponseEntity<DashboardDTOs.MetricsDTO> getMetrics() {
        return ResponseEntity.ok(dashboardService.getMetrics());
    }

    /**
     * GET /api/dashboard/attacks
     * Returns recent detected attack events (latest 50, descending)
     */
    @GetMapping("/attacks")
    public ResponseEntity<List<DashboardDTOs.AttackDTO>> getAttacks() {
        return ResponseEntity.ok(dashboardService.getRecentAttacks());
    }

    /**
     * GET /api/dashboard/top-offenders
     * Returns top 5 attacking IPs with request count and block status
     */
    @GetMapping("/top-offenders")
    public ResponseEntity<List<DashboardDTOs.OffenderDTO>> getTopOffenders() {
        return ResponseEntity.ok(dashboardService.getTopOffenders());
    }

    /**
     * GET /api/dashboard/traffic
     * Returns 24-hour time-series traffic metrics for the chart
     */
    @GetMapping("/traffic")
    public ResponseEntity<List<DashboardDTOs.TrafficPointDTO>> getTraffic() {
        return ResponseEntity.ok(dashboardService.getTrafficSeries());
    }
}
