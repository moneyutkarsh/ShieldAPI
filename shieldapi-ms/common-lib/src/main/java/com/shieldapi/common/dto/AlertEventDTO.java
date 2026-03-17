package com.shieldapi.common.dto;

import com.shieldapi.common.model.Severity;
import java.time.LocalDateTime;

public class AlertEventDTO {
    private String alertId;
    private String title;
    private String message;
    private Severity severity;
    private String source;
    private LocalDateTime timestamp;

    public AlertEventDTO() {}

    public AlertEventDTO(String alertId, String title, String message, Severity severity, String source, LocalDateTime timestamp) {
        this.alertId = alertId;
        this.title = title;
        this.message = message;
        this.severity = severity;
        this.source = source;
        this.timestamp = timestamp;
    }

    // Getters
    public String getAlertId() { return alertId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Severity getSeverity() { return severity; }
    public String getSource() { return source; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Setters
    public void setAlertId(String alertId) { this.alertId = alertId; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public void setSource(String source) { this.source = source; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // Builder emulation
    public static class AlertEventDTOBuilder {
        private String alertId;
        private String title;
        private String message;
        private Severity severity;
        private String source;
        private LocalDateTime timestamp;

        public AlertEventDTOBuilder alertId(String alertId) { this.alertId = alertId; return this; }
        public AlertEventDTOBuilder title(String title) { this.title = title; return this; }
        public AlertEventDTOBuilder message(String message) { this.message = message; return this; }
        public AlertEventDTOBuilder severity(Severity severity) { this.severity = severity; return this; }
        public AlertEventDTOBuilder source(String source) { this.source = source; return this; }
        public AlertEventDTOBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public AlertEventDTO build() {
            return new AlertEventDTO(alertId, title, message, severity, source, timestamp);
        }
    }

    public static AlertEventDTOBuilder builder() {
        return new AlertEventDTOBuilder();
    }
}
