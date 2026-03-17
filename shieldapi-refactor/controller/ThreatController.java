package com.shieldapi.shieldapi.controller;

import com.shieldapi.shieldapi.dto.ThreatResponse;
import com.shieldapi.shieldapi.service.ThreatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing threats.
 */
@RestController
@RequestMapping("/api/v1/threats")
public class ThreatController {

    private final ThreatService threatService;

    public ThreatController(ThreatService threatService) {
        this.threatService = threatService;
    }

    @GetMapping
    public List<ThreatResponse> getAllThreats() {
        return threatService.getAllThreats();
    }

    @GetMapping("/{id}")
    public ThreatResponse getThreatById(@PathVariable Long id) {
        return threatService.getThreatById(id);
    }
}
