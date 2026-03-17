package com.shieldapi.dto;

import com.shieldapi.security.threatintel.Severity;
import com.shieldapi.security.threatintel.ThreatCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatEventDTO {
    private Long id;
    private String ipAddress;
    private String endpoint;
    private ThreatCategory threatCategory;
    private Severity severity;
    private int attemptCount;
    private LocalDateTime detectedAt;
}
