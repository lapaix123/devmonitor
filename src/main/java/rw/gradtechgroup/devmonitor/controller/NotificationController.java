package rw.gradtechgroup.devmonitor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.gradtechgroup.devmonitor.entity.Notification;
import rw.gradtechgroup.devmonitor.security.UserPrincipal;
import rw.gradtechgroup.devmonitor.service.NotificationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List user notifications", description = "Get all notifications for authenticated user")
    public ResponseEntity<?> getNotifications(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<Notification> notifications = notificationService.getUserNotifications(userPrincipal.getId());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Mark specific notification as read")
    public ResponseEntity<?> markAsRead(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            notificationService.markAsRead(id, userPrincipal.getId());
            return ResponseEntity.ok().body("Notification marked as read!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}