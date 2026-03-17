package com.shieldapi.security.repository;

import com.shieldapi.security.model.ThreatEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreatEventRepository extends JpaRepository<ThreatEvent, Long> {
    List<ThreatEvent> findByIpAddress(String ipAddress);
}
