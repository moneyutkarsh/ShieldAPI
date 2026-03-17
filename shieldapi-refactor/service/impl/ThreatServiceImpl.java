package com.shieldapi.shieldapi.service.impl;

import com.shieldapi.shieldapi.dto.ThreatResponse;
import com.shieldapi.shieldapi.service.ThreatService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of ThreatService.
 * Contains business logic for managing threats.
 */
@Service
public class ThreatServiceImpl implements ThreatService {

    @Override
    public List<ThreatResponse> getAllThreats() {
        // TODO: Implement with ThreatRepository
        return Collections.emptyList();
    }

    @Override
    public ThreatResponse getThreatById(Long id) {
        // TODO: Implement with ThreatRepository
        return null;
    }
}
