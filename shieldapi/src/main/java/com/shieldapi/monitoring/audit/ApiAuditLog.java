package com.shieldapi.monitoring.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;

    private String requestUri;

    private String httpMethod;

    private int responseStatus;

    private long responseTime; // in milliseconds

    private Long userId;

    private LocalDateTime timestamp;
}
