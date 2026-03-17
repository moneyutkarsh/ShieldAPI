package com.shieldapi.analytics.service;

import com.shieldapi.common.dto.SystemMetricDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemMetricPublisher {

    private static final Logger log = LoggerFactory.getLogger(SystemMetricPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "system-metrics";

    public void publishMetric(String name, Double value, String unit, Map<String, String> tags) {
        SystemMetricDTO metric = SystemMetricDTO.builder()
                .metricName(name)
                .value(value)
                .unit(unit)
                .timestamp(LocalDateTime.now())
                .tags(tags)
                .build();
        
        kafkaTemplate.send(TOPIC, name, metric);
        log.info("System metric published: {} = {} {}", name, value, unit);
    }
}
