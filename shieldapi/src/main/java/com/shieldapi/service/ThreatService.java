package com.shieldapi.service;

import com.shieldapi.dto.ThreatRequestDTO;
import com.shieldapi.dto.ThreatResponseDTO;
import java.util.List;

public interface ThreatService {
    ThreatResponseDTO createThreat(ThreatRequestDTO requestDTO);
    ThreatResponseDTO getThreatById(Long id);
    List<ThreatResponseDTO> getAllThreats();
    ThreatResponseDTO updateThreat(Long id, ThreatRequestDTO requestDTO);
    void deleteThreat(Long id);
}
