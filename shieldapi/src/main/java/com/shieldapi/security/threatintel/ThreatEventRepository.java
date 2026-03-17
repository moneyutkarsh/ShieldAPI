package com.shieldapi.security.threatintel;

import com.shieldapi.analytics.dto.TopAttackerDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ThreatEventRepository extends JpaRepository<ThreatEvent, Long> {
    Optional<ThreatEvent> findTopByIpAddressAndThreatCategoryOrderByDetectedAtDesc(String ipAddress, ThreatCategory threatCategory);

    @Query("SELECT new com.shieldapi.analytics.dto.TopAttackerDTO(t.ipAddress, SUM(t.attemptCount)) " +
           "FROM ThreatEvent t GROUP BY t.ipAddress ORDER BY SUM(t.attemptCount) DESC")
    List<TopAttackerDTO> findTopAttackers();
    
    long countByDetectedAtAfter(LocalDateTime since);
}
