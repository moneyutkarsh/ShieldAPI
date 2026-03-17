package com.shieldapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "threats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Threat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String severity;

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime detectedAt;

    @PrePersist
    protected void onCreate() {
        this.detectedAt = LocalDateTime.now();
    }
}
