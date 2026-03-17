package com.shieldapi.security.threatintel;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "threat_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThreatEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String endpoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ThreatCategory threatCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false)
    private int attemptCount;

    @Column(nullable = false)
    private LocalDateTime detectedAt;

    @PrePersist
    protected void onCreate() {
        this.detectedAt = LocalDateTime.now();
    }
}
