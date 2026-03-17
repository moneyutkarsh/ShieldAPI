package com.shieldapi.security.threatintel;

import com.shieldapi.monitoring.metrics.SecurityMetricsService;
import com.shieldapi.security.events.SecurityEvent;
import com.shieldapi.security.events.SecurityEventPublisher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ThreatIntelligenceService threatIntelligenceService;
    private final SecurityMetricsService securityMetricsService;
    private final SecurityEventPublisher securityEventPublisher;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String clientIp = getClientIp(request);
        threatIntelligenceService.recordThreat(clientIp, request.getRequestURI(), ThreatCategory.BRUTE_FORCE);
        securityMetricsService.incrementAuthFailures();

        // Publish Security Event to Kafka
        securityEventPublisher.publish(SecurityEvent.builder()
                .eventType("AUTH_FAILURE")
                .sourceIp(clientIp)
                .endpoint(request.getRequestURI())
                .severity("MEDIUM")
                .details(Map.of(
                        "action", "AUTHENTICATION_FAILED",
                        "exception", exception.getMessage()
                ))
                .build());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Authentication failed. Attempt recorded.\"}");
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
