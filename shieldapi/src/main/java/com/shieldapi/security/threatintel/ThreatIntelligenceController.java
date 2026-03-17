package com.shieldapi.security.threatintel;

import com.shieldapi.dto.ThreatEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/threat-intel")
@RequiredArgsConstructor
public class ThreatIntelligenceController {

    private final ThreatIntelligenceService threatIntelligenceService;

    @GetMapping("/events")
    public ResponseEntity<List<ThreatEventDTO>> getAllEvents() {
        return ResponseEntity.ok(threatIntelligenceService.getAllThreatEvents());
    }

    @GetMapping("/events/{ip}")
    public ResponseEntity<List<ThreatEventDTO>> getEventsByIp(@PathVariable String ip) {
        return ResponseEntity.ok(threatIntelligenceService.getThreatEventsByIp(ip));
    }
}
