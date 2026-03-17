package com.shieldapi.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_ip_addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockedIpAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ipAddress;

    private String reason;
    private int threatScore;
    private LocalDateTime blockedAt;
    private LocalDateTime expiresAt;
}
