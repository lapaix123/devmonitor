package rw.gradtechgroup.devmonitor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.gradtechgroup.devmonitor.dto.TeamMemberRequest;
import rw.gradtechgroup.devmonitor.dto.TeamRequest;
import rw.gradtechgroup.devmonitor.entity.Team;
import rw.gradtechgroup.devmonitor.entity.TeamMember;
import rw.gradtechgroup.devmonitor.security.UserPrincipal;
import rw.gradtechgroup.devmonitor.service.TeamService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamRequest request, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Team team = teamService.createTeam(request.getName(), userPrincipal.getId());
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getTeams(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<Team> teams = teamService.getUserTeams(userPrincipal.getId());
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTeam(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            if (!teamService.hasTeamAccess(userPrincipal.getId(), id, TeamMember.TeamRole.MEMBER)) {
                return ResponseEntity.badRequest().body("Error: Access denied");
            }
            
            Team team = teamService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Team not found"));
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable UUID id, @RequestBody TeamRequest request, 
                                       Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Team team = teamService.updateTeam(id, request.getName(), userPrincipal.getId());
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            teamService.deleteTeam(id, userPrincipal.getId());
            return ResponseEntity.ok().body("Team deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<?> addTeamMember(@PathVariable UUID id, @RequestBody TeamMemberRequest request,
                                          Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            TeamMember member = teamService.addTeamMember(id, request.getEmail(), request.getRole(), userPrincipal.getId());
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<?> getTeamMembers(@PathVariable UUID id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            if (!teamService.hasTeamAccess(userPrincipal.getId(), id, TeamMember.TeamRole.MEMBER)) {
                return ResponseEntity.badRequest().body("Error: Access denied");
            }
            
            List<TeamMember> members = teamService.getTeamMembers(id);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeTeamMember(@PathVariable UUID id, @PathVariable UUID userId,
                                             Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            teamService.removeTeamMember(id, userId, userPrincipal.getId());
            return ResponseEntity.ok().body("Team member removed successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}