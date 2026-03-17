package com.shieldapi.security.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEvent {
    private String eventId;
    private LocalDateTime timestamp;
    private String eventType; // e.g., THREAT_DETECTED, RATE_LIMIT_VIOLATION, IP_BLACKLISTED, AUTH_FAILURE
    private String sourceIp;
    private String severity;
    private String endpoint;
    private Map<String, Object> details;
}
