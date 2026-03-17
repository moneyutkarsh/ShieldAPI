package com.shieldapi.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatResponseDTO {
    private Long id;
    private String type;
    private String severity;
    private String description;
    private LocalDateTime detectedAt;
}
