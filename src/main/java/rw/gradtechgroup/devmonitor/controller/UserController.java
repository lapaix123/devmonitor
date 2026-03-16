package rw.gradtechgroup.devmonitor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.gradtechgroup.devmonitor.entity.User;
import rw.gradtechgroup.devmonitor.security.UserPrincipal;
import rw.gradtechgroup.devmonitor.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Get authenticated user's profile information")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findById(userPrincipal.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/me")
    @Operation(summary = "Update user profile", description = "Update authenticated user's profile information")
    public ResponseEntity<?> updateCurrentUser(@RequestBody UpdateUserRequest request, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.updateUser(userPrincipal.getId(), request.getName(), request.getEmail());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete user account", description = "Deactivate authenticated user's account")
    public ResponseEntity<?> deleteCurrentUser(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            userService.deactivateUser(userPrincipal.getId());
            return ResponseEntity.ok().body("User account deactivated successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "List all users (admin)", description = "Get list of all users - admin only")
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        try {
            // TODO: Add admin role check
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific user (admin)", description = "Get user by ID - admin only")
    public ResponseEntity<?> getUser(@PathVariable UUID id, Authentication authentication) {
        try {
            // TODO: Add admin role check
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    public static class UpdateUserRequest {
        private String name;
        private String email;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}