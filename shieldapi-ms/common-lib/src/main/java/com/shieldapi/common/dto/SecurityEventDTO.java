package com.shieldapi.common.dto;

import com.shieldapi.common.model.Severity;
import com.shieldapi.common.model.ThreatCategory;
import java.time.LocalDateTime;

public class SecurityEventDTO {
    private String eventId;
    private String ipAddress;
    private String endpoint;
    private ThreatCategory category;
    private Severity severity;
    private String message;
    private LocalDateTime timestamp;
    private int attemptCount;

    public SecurityEventDTO() {}

    public SecurityEventDTO(String eventId, String ipAddress, String endpoint, ThreatCategory category, Severity severity, String message, LocalDateTime timestamp, int attemptCount) {
        this.eventId = eventId;
        this.ipAddress = ipAddress;
        this.endpoint = endpoint;
        this.category = category;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
        this.attemptCount = attemptCount;
    }

    // Getters
    public String getEventId() { return eventId; }
    public String getIpAddress() { return ipAddress; }
    public String getEndpoint() { return endpoint; }
    public ThreatCategory getCategory() { return category; }
    public Severity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getAttemptCount() { return attemptCount; }

    // Setters
    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public void setCategory(ThreatCategory category) { this.category = category; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }

    // Builder emulation
    public static class SecurityEventDTOBuilder {
        private String eventId;
        private String ipAddress;
        private String endpoint;
        private ThreatCategory category;
        private Severity severity;
        private String message;
        private LocalDateTime timestamp;
        private int attemptCount;

        public SecurityEventDTOBuilder eventId(String eventId) { this.eventId = eventId; return this; }
        public SecurityEventDTOBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public SecurityEventDTOBuilder endpoint(String endpoint) { this.endpoint = endpoint; return this; }
        public SecurityEventDTOBuilder category(ThreatCategory category) { this.category = category; return this; }
        public SecurityEventDTOBuilder severity(Severity severity) { this.severity = severity; return this; }
        public SecurityEventDTOBuilder message(String message) { this.message = message; return this; }
        public SecurityEventDTOBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public SecurityEventDTOBuilder attemptCount(int attemptCount) { this.attemptCount = attemptCount; return this; }
        
        public SecurityEventDTO build() {
            return new SecurityEventDTO(eventId, ipAddress, endpoint, category, severity, message, timestamp, attemptCount);
        }
    }

    public static SecurityEventDTOBuilder builder() {
        return new SecurityEventDTOBuilder();
    }
}
