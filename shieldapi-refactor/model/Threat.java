package com.shieldapi.shieldapi.model;

import jakarta.persistence.*;

/**
 * Threat entity - represents a security threat in the system.
 */
@Entity
@Table(name = "threats")
public class Threat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private String status;   // ACTIVE, RESOLVED, MONITORING

    public Threat() {}

    public Threat(String name, String description, String severity, String status) {
        this.name = name;
        this.description = description;
        this.severity = severity;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
