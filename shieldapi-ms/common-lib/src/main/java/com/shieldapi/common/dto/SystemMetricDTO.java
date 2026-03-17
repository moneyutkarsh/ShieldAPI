package com.shieldapi.common.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class SystemMetricDTO {
    private String metricName;
    private Double value;
    private String unit;
    private LocalDateTime timestamp;
    private Map<String, String> tags;

    public SystemMetricDTO() {}

    public SystemMetricDTO(String metricName, Double value, String unit, LocalDateTime timestamp, Map<String, String> tags) {
        this.metricName = metricName;
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    // Getters
    public String getMetricName() { return metricName; }
    public Double getValue() { return value; }
    public String getUnit() { return unit; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<String, String> getTags() { return tags; }

    // Setters
    public void setMetricName(String metricName) { this.metricName = metricName; }
    public void setValue(Double value) { this.value = value; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setTags(Map<String, String> tags) { this.tags = tags; }

    // Builder emulation
    public static class SystemMetricDTOBuilder {
        private String metricName;
        private Double value;
        private String unit;
        private LocalDateTime timestamp;
        private Map<String, String> tags;

        public SystemMetricDTOBuilder metricName(String metricName) { this.metricName = metricName; return this; }
        public SystemMetricDTOBuilder value(Double value) { this.value = value; return this; }
        public SystemMetricDTOBuilder unit(String unit) { this.unit = unit; return this; }
        public SystemMetricDTOBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public SystemMetricDTOBuilder tags(Map<String, String> tags) { this.tags = tags; return this; }

        public SystemMetricDTO build() {
            return new SystemMetricDTO(metricName, value, unit, timestamp, tags);
        }
    }

    public static SystemMetricDTOBuilder builder() {
        return new SystemMetricDTOBuilder();
    }
}
