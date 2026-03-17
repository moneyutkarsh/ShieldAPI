package com.shieldapi.security.ipreputation;

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

    @Column(nullable = false, unique = true)
    private String ipAddress;

    @Column(nullable = false)
    private String reason;

    private int threatScore;

    @Column(nullable = false)
    private LocalDateTime blockedAt;

    private LocalDateTime expiresAt;
}
