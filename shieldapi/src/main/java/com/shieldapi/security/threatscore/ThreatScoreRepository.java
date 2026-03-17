package com.shieldapi.security.threatscore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThreatScoreRepository extends JpaRepository<ThreatScore, Long> {
    Optional<ThreatScore> findByIpAddress(String ipAddress);

    long countByCurrentScoreGreaterThanEqual(int threshold);
    long countByCurrentScoreBetween(int min, int max);
    long countByCurrentScoreLessThan(int threshold);
}
