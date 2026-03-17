package com.shieldapi.security.repository;

import com.shieldapi.security.model.BlockedIpAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedIpRepository extends JpaRepository<BlockedIpAddress, Long> {
    Optional<BlockedIpAddress> findByIpAddress(String ipAddress);
}
