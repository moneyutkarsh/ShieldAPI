package com.shieldapi.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityAuditLogDTO {
    private Long id;
    private String ipAddress;
    private String requestUri;
    private String httpMethod;
    private int responseStatus;
    private long responseTime;
    private LocalDateTime timestamp;
}
