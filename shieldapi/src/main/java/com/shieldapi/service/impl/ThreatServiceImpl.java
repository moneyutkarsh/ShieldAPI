package com.shieldapi.service.impl;

import com.shieldapi.dto.ThreatRequestDTO;
import com.shieldapi.dto.ThreatResponseDTO;
import com.shieldapi.exception.ResourceNotFoundException;
import com.shieldapi.mapper.ThreatMapper;
import com.shieldapi.model.Threat;
import com.shieldapi.repository.ThreatRepository;
import com.shieldapi.service.ThreatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThreatServiceImpl implements ThreatService {

    private final ThreatRepository threatRepository;
    private final ThreatMapper threatMapper;

    @Override
    @Transactional
    public ThreatResponseDTO createThreat(ThreatRequestDTO requestDTO) {
        Threat threat = threatMapper.toEntity(requestDTO);
        Threat savedThreat = threatRepository.save(threat);
        return threatMapper.toResponseDTO(savedThreat);
    }

    @Override
    @Transactional(readOnly = true)
    public ThreatResponseDTO getThreatById(Long id) {
        Threat threat = threatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Threat not found with id: " + id));
        return threatMapper.toResponseDTO(threat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThreatResponseDTO> getAllThreats() {
        return threatRepository.findAll().stream()
                .map(threatMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ThreatResponseDTO updateThreat(Long id, ThreatRequestDTO requestDTO) {
        Threat threat = threatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Threat not found with id: " + id));
        threatMapper.updateEntityFromDto(requestDTO, threat);
        Threat updatedThreat = threatRepository.save(threat);
        return threatMapper.toResponseDTO(updatedThreat);
    }

    @Override
    @Transactional
    public void deleteThreat(Long id) {
        if (!threatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Threat not found with id: " + id);
        }
        threatRepository.deleteById(id);
    }
}
