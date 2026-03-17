package com.shieldapi.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopAttackerDTO {
    private String ipAddress;
    private Long attackCount;
}
