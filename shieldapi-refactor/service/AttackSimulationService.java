package com.shieldapi.service;

import com.shieldapi.security.threatintel.Severity;
import com.shieldapi.security.threatintel.ThreatCategory;
import com.shieldapi.security.threatintel.ThreatEvent;
import com.shieldapi.security.threatintel.ThreatEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttackSimulationService {

    private final ThreatEventRepository threatEventRepository;

    public Map<String, String> simulateAttack(String type, String ip, String endpoint, Severity severity) {
        ThreatEvent event = ThreatEvent.builder()
                .threatCategory(ThreatCategory.valueOf(type))
                .ipAddress(ip)
                .endpoint(endpoint)
                .severity(severity)
                .attemptCount(1)
                .detectedAt(LocalDateTime.now())
                .build();

        threatEventRepository.save(event);

        Map<String, String> response = new HashMap<>();
        response.put("attackType", type);
        response.put("ip", ip);
        response.put("endpoint", endpoint);
        response.put("status", "simulated");
        return response;
    }
}
