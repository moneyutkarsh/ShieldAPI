package com.shieldapi.security.service;

import com.shieldapi.security.model.BlockedIpAddress;
import com.shieldapi.security.repository.BlockedIpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class IpReputationService {

    private final BlockedIpRepository blockedIpRepository;

    @org.springframework.cache.annotation.Cacheable(value = "ip-reputation", key = "#ipAddress")
    public boolean isIpBlacklisted(String ipAddress) {
        return blockedIpRepository.findByIpAddress(ipAddress)
                .map(blocked -> {
                    if (blocked.getExpiresAt().isAfter(LocalDateTime.now())) {
                        return true;
                    }
                    blockedIpRepository.delete(blocked);
                    return false;
                }).orElse(false);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "ip-reputation", key = "#ipAddress")
    public void blacklistIp(String ipAddress, String reason, int threatScore) {
        log.warn("Blacklisting IP: {}. Reason: {}", ipAddress, reason);
        
        BlockedIpAddress blocked = BlockedIpAddress.builder()
                .ipAddress(ipAddress)
                .reason(reason)
                .threatScore(threatScore)
                .blockedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        blockedIpRepository.save(blocked);
    }
}
