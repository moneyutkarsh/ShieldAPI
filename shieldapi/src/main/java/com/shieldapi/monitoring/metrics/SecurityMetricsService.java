package com.shieldapi.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SecurityMetricsService {

    private final MeterRegistry meterRegistry;
    private final String NAMESPACE = "shieldapi.security.metrics";
    
    // Counters for specific events
    private final Counter requestCounter;
    private final Counter rateLimitViolationCounter;
    private final Counter authFailureCounter;
    private final Counter threatDetectionCounter;

    public SecurityMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        this.requestCounter = Counter.builder(NAMESPACE + ".requests.total")
                .description("Total number of API requests processed")
                .register(meterRegistry);
                
        this.rateLimitViolationCounter = Counter.builder(NAMESPACE + ".rate_limit.violations")
                .description("Total number of rate limit violations detected")
                .register(meterRegistry);
                
        this.authFailureCounter = Counter.builder(NAMESPACE + ".auth.failures")
                .description("Total number of authentication failures (brute force attempts)")
                .register(meterRegistry);
                
        this.threatDetectionCounter = Counter.builder(NAMESPACE + ".threats.detected")
                .description("Total number of security threats recorded by Threat Intelligence")
                .register(meterRegistry);
    }

    public void incrementRequestCount() {
        requestCounter.increment();
    }

    public void incrementRateLimitViolations() {
        rateLimitViolationCounter.increment();
    }

    public void incrementAuthFailures() {
        authFailureCounter.increment();
    }

    public void incrementThreatsDetected() {
        threatDetectionCounter.increment();
    }
}
