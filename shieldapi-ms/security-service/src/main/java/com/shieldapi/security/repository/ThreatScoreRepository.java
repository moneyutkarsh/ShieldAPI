package com.shieldapi.security.repository;

import com.shieldapi.security.model.ThreatScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThreatScoreRepository extends JpaRepository<ThreatScore, Long> {
    Optional<ThreatScore> findByIpAddress(String ipAddress);
}
