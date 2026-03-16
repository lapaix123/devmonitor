package rw.gradtechgroup.devmonitor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rw.gradtechgroup.devmonitor.dto.JwtResponse;
import rw.gradtechgroup.devmonitor.dto.LoginRequest;
import rw.gradtechgroup.devmonitor.dto.SignupRequest;
import rw.gradtechgroup.devmonitor.entity.User;
import rw.gradtechgroup.devmonitor.security.UserPrincipal;
import rw.gradtechgroup.devmonitor.service.UserService;
import rw.gradtechgroup.devmonitor.util.JwtUtil;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    @Operation(summary = "Create new user account", description = "Register a new user in the system")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        try {
            User.Role role = User.Role.USER;
            if (signUpRequest.getRole() != null) {
                role = User.Role.valueOf(signUpRequest.getRole().toUpperCase());
            }

            User user = userService.createUser(
                    signUpRequest.getName(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword(),
                    role
            );

            return ResponseEntity.ok().body("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT", description = "Login with email and password to get JWT token")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(loginRequest.getEmail());

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            userService.updateLastLogin(userPrincipal.getId());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userPrincipal.getId(),
                    userPrincipal.getName(),
                    userPrincipal.getUsername(),
                    userPrincipal.getAuthorities().iterator().next().getAuthority()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: Invalid credentials");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidate current session/token", description = "Logout current user session")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body("User logged out successfully!");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<?> refreshToken() {
        // TODO: Implement refresh token logic
        return ResponseEntity.ok().body("Refresh token endpoint - to be implemented");
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify user email", description = "Verify user email address with verification token")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        // TODO: Implement email verification logic
        return ResponseEntity.ok().body("Email verification endpoint - to be implemented");
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Send password reset email to user")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        // TODO: Implement forgot password logic
        return ResponseEntity.ok().body("Password reset email sent!");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset user password", description = "Reset password using reset token")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        // TODO: Implement password reset logic
        return ResponseEntity.ok().body("Password reset successfully!");
    }
}