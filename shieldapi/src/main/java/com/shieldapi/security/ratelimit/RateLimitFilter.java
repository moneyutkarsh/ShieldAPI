package com.shieldapi.security.ratelimit;

import com.shieldapi.monitoring.metrics.SecurityMetricsService;
import com.shieldapi.security.events.SecurityEvent;
import com.shieldapi.security.events.SecurityEventPublisher;
import com.shieldapi.security.threatintel.ThreatCategory;
import com.shieldapi.security.threatintel.ThreatIntelligenceService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ThreatIntelligenceService threatIntelligenceService;
    private final SecurityMetricsService securityMetricsService;
    private final SecurityEventPublisher securityEventPublisher;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/auth/") || path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIp(request);
        Bucket bucket = rateLimitService.resolveBucket(clientIp);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            // Record the threat
            threatIntelligenceService.recordThreat(clientIp, request.getRequestURI(), ThreatCategory.RATE_LIMIT_ABUSE);
            securityMetricsService.incrementRateLimitViolations();

            // Publish Security Event to Kafka
            securityEventPublisher.publish(SecurityEvent.builder()
                    .eventType("RATE_LIMIT_VIOLATION")
                    .sourceIp(clientIp)
                    .endpoint(request.getRequestURI())
                    .severity("LOW")
                    .details(Map.of(
                            "action", "REQUEST_BLOCKED",
                            "reason", "RATE_LIMIT_EXCEEDED"
                    ))
                    .build());

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\", \"ip\": \"" + clientIp + "\"}");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
