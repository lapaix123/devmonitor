package rw.gradtechgroup.devmonitor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.gradtechgroup.devmonitor.dto.AlertRuleRequest;
import rw.gradtechgroup.devmonitor.entity.AlertEvent;
import rw.gradtechgroup.devmonitor.entity.AlertRule;
import rw.gradtechgroup.devmonitor.security.UserPrincipal;
import rw.gradtechgroup.devmonitor.service.AlertService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Alerts", description = "Alert management APIs")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @PostMapping("/rules")
    @Operation(summary = "Create alert rule", description = "Create new alert rule for server monitoring")
    public ResponseEntity<?> createAlertRule(@RequestBody AlertRuleRequest request, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            AlertRule alertRule = alertService.createAlertRule(
                    request.getServerId(),
                    request.getCondition(),
                    request.getThreshold(),
                    request.getDurationSeconds(),
                    userPrincipal.getId()
            );
            return ResponseEntity.ok(alertRule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/rules")
    @Operation(summary = "List alert rules", description = "Get all alert rules for user's servers")
    public ResponseEntity<?> getAlertRules(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<AlertRule> rules = alertService.getAlertRules(userPrincipal.getId());
            return ResponseEntity.ok(rules);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/rules/{id}")
    @Operation(summary = "Update alert rule", description = "Update existing alert rule configuration")
    public ResponseEntity<?> updateAlertRule(@PathVariable UUID id, @RequestBody AlertRuleRequest request, 
                                            Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            AlertRule alertRule = alertService.updateAlertRule(
                    id,
                    request.getCondition(),
                    request.getThreshold(),
                    request.getDurationSeconds(),
                    request.getIsActive(),
                    userPrincipal.getId()
            );
            return ResponseEntity.ok(alertRule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/rules/{id}")
    @Operation(summary = "Delete alert rule", description = "Delete alert rule by ID")
    public ResponseEntity<?> deleteAlertRule(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            alertService.deleteAlertRule(id, userPrincipal.getId());
            return ResponseEntity.ok().body("Alert rule deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/events")
    @Operation(summary = "List triggered alerts", description = "Get all alert events for user's servers")
    public ResponseEntity<?> getAlertEvents(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<AlertEvent> events = alertService.getAlertEvents(userPrincipal.getId());
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "Get alert event details", description = "Get specific alert event by ID")
    public ResponseEntity<?> getAlertEvent(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            AlertEvent event = alertService.getAlertEvent(id, userPrincipal.getId())
                    .orElseThrow(() -> new RuntimeException("Alert event not found"));
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}