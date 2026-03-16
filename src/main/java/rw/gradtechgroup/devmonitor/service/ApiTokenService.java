package rw.gradtechgroup.devmonitor.service;

import rw.gradtechgroup.devmonitor.entity.ApiToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ApiTokenService {
    ApiToken createToken(UUID userId, String name, LocalDateTime expiresAt);
    List<ApiToken> getUserTokens(UUID userId);
    void deleteToken(UUID tokenId, UUID userId);
    boolean validateToken(String tokenHash);
}