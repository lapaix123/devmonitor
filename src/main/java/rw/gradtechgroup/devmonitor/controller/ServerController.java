package rw.gradtechgroup.devmonitor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.gradtechgroup.devmonitor.dto.ServerRequest;
import rw.gradtechgroup.devmonitor.entity.Server;
import rw.gradtechgroup.devmonitor.security.UserPrincipal;
import rw.gradtechgroup.devmonitor.service.ServerService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/servers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServerController {

    @Autowired
    private ServerService serverService;

    @PostMapping
    public ResponseEntity<?> createServer(@RequestBody ServerRequest request, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            // For now, we'll need to get teamId from request or use a default team
            // This would typically come from the request or be determined by business logic
            UUID teamId = request.getTeamId(); // Assuming we add teamId to ServerRequest
            
            Server server = serverService.createServer(
                    teamId,
                    request.getName(),
                    request.getIpAddress(),
                    request.getPort(),
                    request.getOs(),
                    request.getEnvironment(),
                    userPrincipal.getId()
            );

            return ResponseEntity.ok(server);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getServers(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<Server> servers = serverService.getUserServers(userPrincipal.getId());
            return ResponseEntity.ok(servers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getServer(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Server server = serverService.findById(id, userPrincipal.getId())
                    .orElseThrow(() -> new RuntimeException("Server not found"));
            return ResponseEntity.ok(server);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateServer(@PathVariable UUID id, @RequestBody ServerRequest request, 
                                         Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            Server server = serverService.updateServer(
                    id,
                    request.getName(),
                    request.getIpAddress(),
                    request.getPort(),
                    request.getOs(),
                    request.getEnvironment(),
                    userPrincipal.getId()
            );

            return ResponseEntity.ok(server);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServer(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            serverService.deleteServer(id, userPrincipal.getId());
            return ResponseEntity.ok().body("Server deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/regenerate-token")
    public ResponseEntity<?> regenerateAgentToken(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Server server = serverService.regenerateAgentToken(id, userPrincipal.getId());
            return ResponseEntity.ok(server);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getServerStatus(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Server server = serverService.findById(id, userPrincipal.getId())
                    .orElseThrow(() -> new RuntimeException("Server not found"));
            
            return ResponseEntity.ok().body(Map.of(
                    "status", server.getStatus(),
                    "lastSeenAt", server.getLastSeenAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}