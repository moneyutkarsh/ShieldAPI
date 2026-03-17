package com.shieldapi.shieldapi.dto;

/**
 * DTO for outgoing threat responses.
 */
public class ThreatResponse {

    private Long id;
    private String name;
    private String severity;
    private String status;

    public ThreatResponse() {}

    public ThreatResponse(Long id, String name, String severity, String status) {
        this.id = id;
        this.name = name;
        this.severity = severity;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
