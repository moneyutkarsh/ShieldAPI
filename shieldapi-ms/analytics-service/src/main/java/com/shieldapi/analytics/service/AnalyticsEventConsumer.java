package com.shieldapi.analytics.service;

import com.shieldapi.common.dto.SecurityEventDTO;
import com.shieldapi.analytics.model.ThreatMetric;
import com.shieldapi.analytics.repository.ThreatMetricRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsEventConsumer.class);

    private final ThreatMetricRepository threatMetricRepository;
    private final SystemMetricPublisher metricPublisher;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 2000, multiplier = 2.0),
        dltTopicSuffix = "-dlt"
    )
    @org.springframework.cache.annotation.CacheEvict(value = {"dashboard-metrics", "recent-attacks"}, allEntries = true)
    @KafkaListener(topics = "security-events", groupId = "analytics-group")
    public void consume(SecurityEventDTO event) {
        log.info("Processing security event for analytics: {}", event.getEventId());
        
        // Update local metrics database
        ThreatMetric metric = ThreatMetric.builder()
                .ipAddress(event.getIpAddress())
                .endpoint(event.getEndpoint())
                .category(event.getCategory().name())
                .severity(event.getSeverity().name())
                .timestamp(event.getTimestamp())
                .attemptCount(event.getAttemptCount())
                .build();
        
        threatMetricRepository.save(metric);

        // Publish aggregated system metrics
        metricPublisher.publishMetric(
                "security.threat.count", 
                (double) event.getAttemptCount(), 
                "count", 
                Map.of("category", event.getCategory().name(), "severity", event.getSeverity().name())
        );
    }

    @DltHandler
    public void handleDlt(SecurityEventDTO event) {
        log.error("Event sent to DLQ: {}", event.getEventId());
    }
}
