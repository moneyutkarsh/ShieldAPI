package com.shieldapi.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "threat_scores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ipAddress;

    private int currentScore;
    private LocalDateTime lastUpdated;
}
