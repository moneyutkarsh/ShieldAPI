package com.shieldapi.analytics.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "threat_metrics")
public class ThreatMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;
    private String endpoint;
    private String category;
    private String severity;
    private LocalDateTime timestamp;
    private int attemptCount;

    public ThreatMetric() {}

    public ThreatMetric(Long id, String ipAddress, String endpoint, String category, String severity, LocalDateTime timestamp, int attemptCount) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.endpoint = endpoint;
        this.category = category;
        this.severity = severity;
        this.timestamp = timestamp;
        this.attemptCount = attemptCount;
    }

    // Getters
    public Long getId() { return id; }
    public String getIpAddress() { return ipAddress; }
    public String getEndpoint() { return endpoint; }
    public String getCategory() { return category; }
    public String getSeverity() { return severity; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getAttemptCount() { return attemptCount; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public void setCategory(String category) { this.category = category; }
    public void setSeverity(String severity) { this.severity = severity; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }

    // Builder
    public static class ThreatMetricBuilder {
        private Long id;
        private String ipAddress;
        private String endpoint;
        private String category;
        private String severity;
        private LocalDateTime timestamp;
        private int attemptCount;

        public ThreatMetricBuilder id(Long id) { this.id = id; return this; }
        public ThreatMetricBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public ThreatMetricBuilder endpoint(String endpoint) { this.endpoint = endpoint; return this; }
        public ThreatMetricBuilder category(String category) { this.category = category; return this; }
        public ThreatMetricBuilder severity(String severity) { this.severity = severity; return this; }
        public ThreatMetricBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public ThreatMetricBuilder attemptCount(int attemptCount) { this.attemptCount = attemptCount; return this; }

        public ThreatMetric build() {
            return new ThreatMetric(id, ipAddress, endpoint, category, severity, timestamp, attemptCount);
        }
    }

    public static ThreatMetricBuilder builder() {
        return new ThreatMetricBuilder();
    }
}
