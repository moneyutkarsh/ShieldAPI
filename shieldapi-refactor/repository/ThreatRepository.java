package com.shieldapi.shieldapi.repository;

import com.shieldapi.shieldapi.model.Threat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Threat entity CRUD operations.
 */
@Repository
public interface ThreatRepository extends JpaRepository<Threat, Long> {
    // Custom query methods can be added here
}
