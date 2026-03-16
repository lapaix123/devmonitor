package rw.gradtechgroup.devmonitor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.gradtechgroup.devmonitor.dto.LogIngestRequest;
import rw.gradtechgroup.devmonitor.entity.Log;
import rw.gradtechgroup.devmonitor.security.UserPrincipal;
import rw.gradtechgroup.devmonitor.service.LogService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Logs", description = "Log management APIs")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("/logs/ingest")
    @Operation(summary = "Receive logs from agent", description = "Ingest logs from monitoring agent")
    public ResponseEntity<?> ingestLogs(@RequestBody LogIngestRequest request) {
        try {
            logService.ingestLogs(request.getAgentToken(), request.getLogs());
            return ResponseEntity.ok().body("Logs ingested successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/servers/{id}/logs")
    @Operation(summary = "Fetch server logs", description = "Get logs for specific server with optional filtering")
    public ResponseEntity<?> getServerLogs(
            @PathVariable UUID id,
            @RequestParam(required = false) String logType,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Authentication authentication) {
        
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            List<Log> logs;
            if (from != null && to != null) {
                logs = logService.getServerLogsInRange(id, from, to, userPrincipal.getId());
            } else if (logType != null) {
                Log.LogType type = Log.LogType.valueOf(logType.toUpperCase());
                logs = logService.getServerLogsByType(id, type, userPrincipal.getId());
            } else if (level != null) {
                Log.LogLevel logLevel = Log.LogLevel.valueOf(level.toUpperCase());
                logs = logService.getServerLogsByLevel(id, logLevel, userPrincipal.getId());
            } else {
                logs = logService.getServerLogs(id, userPrincipal.getId());
            }
            
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}