package rw.gradtechgroup.devmonitor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gradtechgroup.devmonitor.entity.Team;
import rw.gradtechgroup.devmonitor.entity.TeamMember;
import rw.gradtechgroup.devmonitor.entity.User;
import rw.gradtechgroup.devmonitor.repository.TeamMemberRepository;
import rw.gradtechgroup.devmonitor.repository.TeamRepository;
import rw.gradtechgroup.devmonitor.repository.UserRepository;
import rw.gradtechgroup.devmonitor.service.AuditService;
import rw.gradtechgroup.devmonitor.service.TeamService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditService auditService;

    public Team createTeam(String name, UUID ownerId) {
        User owner = userRepository.findByIdAndIsActiveTrue(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Team team = new Team();
        team.setName(name);
        team.setOwner(owner);

        Team savedTeam = teamRepository.save(team);

        // Add owner as team member
        TeamMember ownerMember = new TeamMember();
        ownerMember.setTeam(savedTeam);
        ownerMember.setUser(owner);
        ownerMember.setRole(TeamMember.TeamRole.OWNER);
        teamMemberRepository.save(ownerMember);

        auditService.logAction(ownerId, "CREATE_TEAM", "Team", savedTeam.getId().toString(), null);
        
        return savedTeam;
    }

    public List<Team> getUserTeams(UUID userId) {
        List<TeamMember> memberships = teamMemberRepository.findByUserId(userId);
        return memberships.stream()
                .map(TeamMember::getTeam)
                .collect(Collectors.toList());
    }

    public Optional<Team> findById(UUID id) {
        return teamRepository.findById(id);
    }

    public Team updateTeam(UUID teamId, String name, UUID userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!hasTeamAccess(userId, teamId, TeamMember.TeamRole.ADMIN)) {
            throw new RuntimeException("Access denied");
        }

        team.setName(name);
        Team updatedTeam = teamRepository.save(team);
        
        auditService.logAction(userId, "UPDATE_TEAM", "Team", teamId.toString(), null);
        
        return updatedTeam;
    }

    public void deleteTeam(UUID teamId, UUID userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!team.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Only team owner can delete team");
        }

        teamRepository.delete(team);
        auditService.logAction(userId, "DELETE_TEAM", "Team", teamId.toString(), null);
    }

    public TeamMember addTeamMember(UUID teamId, String email, TeamMember.TeamRole role, UUID requesterId) {
        if (!hasTeamAccess(requesterId, teamId, TeamMember.TeamRole.ADMIN)) {
            throw new RuntimeException("Access denied");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId())) {
            throw new RuntimeException("User is already a team member");
        }

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setUser(user);
        member.setRole(role);

        TeamMember savedMember = teamMemberRepository.save(member);
        auditService.logAction(requesterId, "ADD_TEAM_MEMBER", "TeamMember", savedMember.getId().toString(), null);
        
        return savedMember;
    }

    public void removeTeamMember(UUID teamId, UUID userId, UUID requesterId) {
        if (!hasTeamAccess(requesterId, teamId, TeamMember.TeamRole.ADMIN)) {
            throw new RuntimeException("Access denied");
        }

        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new RuntimeException("Team member not found"));

        if (member.getRole() == TeamMember.TeamRole.OWNER) {
            throw new RuntimeException("Cannot remove team owner");
        }

        teamMemberRepository.delete(member);
        auditService.logAction(requesterId, "REMOVE_TEAM_MEMBER", "TeamMember", member.getId().toString(), null);
    }

    public List<TeamMember> getTeamMembers(UUID teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    public boolean hasTeamAccess(UUID userId, UUID teamId, TeamMember.TeamRole minimumRole) {
        Optional<TeamMember> membership = teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
        if (membership.isEmpty()) {
            return false;
        }

        TeamMember.TeamRole userRole = membership.get().getRole();
        return hasRequiredRole(userRole, minimumRole);
    }

    private boolean hasRequiredRole(TeamMember.TeamRole userRole, TeamMember.TeamRole requiredRole) {
        int userLevel = getRoleLevel(userRole);
        int requiredLevel = getRoleLevel(requiredRole);
        return userLevel >= requiredLevel;
    }

    private int getRoleLevel(TeamMember.TeamRole role) {
        switch (role) {
            case MEMBER: return 1;
            case ADMIN: return 2;
            case OWNER: return 3;
            default: return 0;
        }
    }

    public List<UUID> getUserTeamIds(UUID userId) {
        return teamMemberRepository.findByUserId(userId).stream()
                .map(tm -> tm.getTeam().getId())
                .collect(Collectors.toList());
    }
}