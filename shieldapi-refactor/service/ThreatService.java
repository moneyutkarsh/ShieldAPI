package com.shieldapi.shieldapi.service;

import com.shieldapi.shieldapi.dto.ThreatResponse;
import java.util.List;

/**
 * Service interface for threat-related business logic.
 */
public interface ThreatService {

    List<ThreatResponse> getAllThreats();

    ThreatResponse getThreatById(Long id);
}
