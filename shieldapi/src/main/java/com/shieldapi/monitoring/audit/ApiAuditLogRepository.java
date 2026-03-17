package com.shieldapi.monitoring.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiAuditLogRepository extends JpaRepository<ApiAuditLog, Long> {

    @Query("SELECT AVG(a.responseTime) FROM ApiAuditLog a")
    Double getAverageResponseTime();

    @Query("SELECT a.responseStatus, COUNT(a) FROM ApiAuditLog a GROUP BY a.responseStatus")
    List<Object[]> getStatusCodeDistribution();
}
