package rw.gradtechgroup.devmonitor.service;

import rw.gradtechgroup.devmonitor.entity.Team;
import rw.gradtechgroup.devmonitor.entity.TeamMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamService {
    Team createTeam(String name, UUID ownerId);
    List<Team> getUserTeams(UUID userId);
    Optional<Team> findById(UUID id);
    Team updateTeam(UUID teamId, String name, UUID userId);
    void deleteTeam(UUID teamId, UUID userId);
    TeamMember addTeamMember(UUID teamId, String email, TeamMember.TeamRole role, UUID requesterId);
    void removeTeamMember(UUID teamId, UUID userId, UUID requesterId);
    List<TeamMember> getTeamMembers(UUID teamId);
    boolean hasTeamAccess(UUID userId, UUID teamId, TeamMember.TeamRole minimumRole);
    List<UUID> getUserTeamIds(UUID userId);
}