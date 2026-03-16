package rw.gradtechgroup.devmonitor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gradtechgroup.devmonitor.entity.ApiToken;
import rw.gradtechgroup.devmonitor.entity.User;
import rw.gradtechgroup.devmonitor.repository.ApiTokenRepository;
import rw.gradtechgroup.devmonitor.repository.UserRepository;
import rw.gradtechgroup.devmonitor.service.ApiTokenService;
import rw.gradtechgroup.devmonitor.service.AuditService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ApiTokenServiceImpl implements ApiTokenService {

    @Autowired
    private ApiTokenRepository apiTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditService auditService;

    @Override
    public ApiToken createToken(UUID userId, String name, LocalDateTime expiresAt) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String rawToken = UUID.randomUUID().toString().replace("-", "");
        String tokenHash = passwordEncoder.encode(rawToken);

        ApiToken apiToken = new ApiToken();
        apiToken.setUser(user);
        apiToken.setName(name);
        apiToken.setTokenHash(tokenHash);
        apiToken.setExpiresAt(expiresAt);

        ApiToken savedToken = apiTokenRepository.save(apiToken);
        auditService.logAction(userId, "CREATE_API_TOKEN", "ApiToken", savedToken.getId().toString(), null);

        // Return token with raw value for one-time display
        savedToken.setTokenHash(rawToken); // Temporarily set raw token for response
        return savedToken;
    }

    @Override
    public List<ApiToken> getUserTokens(UUID userId) {
        return apiTokenRepository.findByUserId(userId);
    }

    @Override
    public void deleteToken(UUID tokenId, UUID userId) {
        ApiToken token = apiTokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (!token.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        apiTokenRepository.delete(token);
        auditService.logAction(userId, "DELETE_API_TOKEN", "ApiToken", tokenId.toString(), null);
    }

    @Override
    public boolean validateToken(String tokenHash) {
        return apiTokenRepository.findByTokenHash(tokenHash)
                .map(token -> token.getExpiresAt() == null || token.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}