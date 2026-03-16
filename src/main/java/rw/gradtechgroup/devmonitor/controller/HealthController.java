package rw.gradtechgroup.devmonitor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Health & System", description = "System health and information APIs")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Check API status", description = "Health check endpoint for API status")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "DevMonitor API"
        ));
    }

    @GetMapping("/version")
    @Operation(summary = "Get API version info", description = "Get API version and build information")
    public ResponseEntity<?> getVersion() {
        return ResponseEntity.ok(Map.of(
                "version", "1.0.0",
                "buildTime", "2024-01-01T00:00:00",
                "gitCommit", "abc123",
                "environment", "development"
        ));
    }
}