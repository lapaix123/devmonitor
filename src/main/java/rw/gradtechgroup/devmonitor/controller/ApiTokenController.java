package rw.gradtechgroup.devmonitor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.gradtechgroup.devmonitor.dto.ApiTokenRequest;
import rw.gradtechgroup.devmonitor.entity.ApiToken;
import rw.gradtechgroup.devmonitor.security.UserPrincipal;
import rw.gradtechgroup.devmonitor.service.ApiTokenService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tokens")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "API Tokens", description = "API token management APIs")
public class ApiTokenController {

    @Autowired
    private ApiTokenService apiTokenService;

    @PostMapping
    @Operation(summary = "Create API token", description = "Create new API token for authentication")
    public ResponseEntity<?> createApiToken(@RequestBody ApiTokenRequest request, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            ApiToken token = apiTokenService.createToken(userPrincipal.getId(), request.getName(), request.getExpiresAt());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "List API tokens", description = "Get all API tokens for authenticated user")
    public ResponseEntity<?> getApiTokens(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<ApiToken> tokens = apiTokenService.getUserTokens(userPrincipal.getId());
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete API token", description = "Delete API token by ID")
    public ResponseEntity<?> deleteApiToken(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            apiTokenService.deleteToken(id, userPrincipal.getId());
            return ResponseEntity.ok().body("API token deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}