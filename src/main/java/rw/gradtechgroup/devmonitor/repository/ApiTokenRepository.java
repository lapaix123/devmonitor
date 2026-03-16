package rw.gradtechgroup.devmonitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.gradtechgroup.devmonitor.entity.ApiToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiTokenRepository extends JpaRepository<ApiToken, UUID> {
    
    List<ApiToken> findByUserId(UUID userId);
    
    Optional<ApiToken> findByTokenHash(String tokenHash);
    
    List<ApiToken> findByExpiresAtBefore(LocalDateTime dateTime);
}