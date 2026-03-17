package com.shieldapi.controller;

import com.shieldapi.dto.ThreatRequestDTO;
import com.shieldapi.dto.ThreatResponseDTO;
import com.shieldapi.service.ThreatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/threats")
@RequiredArgsConstructor
public class ThreatController {

    private final ThreatService threatService;

    @PostMapping
    public ResponseEntity<ThreatResponseDTO> createThreat(@Valid @RequestBody ThreatRequestDTO requestDTO) {
        return new ResponseEntity<>(threatService.createThreat(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThreatResponseDTO> getThreatById(@PathVariable Long id) {
        return ResponseEntity.ok(threatService.getThreatById(id));
    }

    @GetMapping
    public ResponseEntity<List<ThreatResponseDTO>> getAllThreats() {
        return ResponseEntity.ok(threatService.getAllThreats());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThreatResponseDTO> updateThreat(@PathVariable Long id, @Valid @RequestBody ThreatRequestDTO requestDTO) {
        return ResponseEntity.ok(threatService.updateThreat(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteThreat(@PathVariable Long id) {
        threatService.deleteThreat(id);
        return ResponseEntity.noContent().build();
    }
}
