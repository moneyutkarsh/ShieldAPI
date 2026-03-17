package com.shieldapi.analytics.repository;

import com.shieldapi.analytics.model.ThreatMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreatMetricRepository extends JpaRepository<ThreatMetric, Long> {
}
