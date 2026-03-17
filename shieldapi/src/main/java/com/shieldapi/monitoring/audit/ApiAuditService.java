package com.shieldapi.monitoring.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiAuditService {

    private final ApiAuditLogRepository apiAuditLogRepository;

    @Async
    public void saveAuditLog(ApiAuditLog auditLog) {
        try {
            apiAuditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to persist API audit log asynchronously", e);
        }
    }
}
