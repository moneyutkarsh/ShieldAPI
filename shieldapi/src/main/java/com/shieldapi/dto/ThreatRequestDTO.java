package com.shieldapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatRequestDTO {

    @NotBlank(message = "Threat type is required")
    private String type;

    @NotBlank(message = "Severity is required")
    private String severity;

    private String description;
}
