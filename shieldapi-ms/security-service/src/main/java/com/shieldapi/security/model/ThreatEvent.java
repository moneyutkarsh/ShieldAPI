package com.shieldapi.security.model;

import com.shieldapi.common.model.Severity;
import com.shieldapi.common.model.ThreatCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "threat_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ThreatCategory threatCategory;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    private String ipAddress;
    private String endpoint;
    private LocalDateTime detectedAt;
    private int attemptCount;
}
