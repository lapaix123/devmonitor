package rw.gradtechgroup.devmonitor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gradtechgroup.devmonitor.entity.Server;
import rw.gradtechgroup.devmonitor.entity.Team;
import rw.gradtechgroup.devmonitor.entity.TeamMember;
import rw.gradtechgroup.devmonitor.repository.ServerRepository;
import rw.gradtechgroup.devmonitor.repository.TeamRepository;
import rw.gradtechgroup.devmonitor.service.AuditService;
import rw.gradtechgroup.devmonitor.service.ServerService;
import rw.gradtechgroup.devmonitor.service.TeamService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ServerServiceImpl implements ServerService {

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private AuditService auditService;

    public Server createServer(UUID teamId, String name, String ipAddress, Integer port, String os, 
                              Server.Environment environment, UUID userId) {
        if (!teamService.hasTeamAccess(userId, teamId, TeamMember.TeamRole.MEMBER)) {
            throw new RuntimeException("Access denied");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        Server server = new Server();
        server.setTeam(team);
        server.setName(name);
        server.setIpAddress(ipAddress);
        server.setPort(port != null ? port : 22);
        server.setOs(os);
        server.setEnvironment(environment);
        server.setStatus(Server.Status.OFFLINE);
        server.setAgentToken(generateAgentToken());

        Server savedServer = serverRepository.save(server);
        auditService.logAction(userId, "CREATE_SERVER", "Server", savedServer.getId().toString(), null);
        
        return savedServer;
    }

    public List<Server> getUserServers(UUID userId) {
        List<UUID> teamIds = teamService.getUserTeamIds(userId);
        return serverRepository.findByTeamIdIn(teamIds);
    }

    public Optional<Server> findById(UUID id, UUID userId) {
        Optional<Server> server = serverRepository.findById(id);
        if (server.isPresent()) {
            if (!teamService.hasTeamAccess(userId, server.get().getTeam().getId(), TeamMember.TeamRole.MEMBER)) {
                return Optional.empty();
            }
        }
        return server;
    }

    public Server updateServer(UUID serverId, String name, String ipAddress, Integer port, String os, 
                              Server.Environment environment, UUID userId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        if (!teamService.hasTeamAccess(userId, server.getTeam().getId(), TeamMember.TeamRole.MEMBER)) {
            throw new RuntimeException("Access denied");
        }

        server.setName(name);
        server.setIpAddress(ipAddress);
        server.setPort(port != null ? port : server.getPort());
        server.setOs(os);
        server.setEnvironment(environment);

        Server updatedServer = serverRepository.save(server);
        auditService.logAction(userId, "UPDATE_SERVER", "Server", serverId.toString(), null);
        
        return updatedServer;
    }

    public void deleteServer(UUID serverId, UUID userId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        if (!teamService.hasTeamAccess(userId, server.getTeam().getId(), TeamMember.TeamRole.ADMIN)) {
            throw new RuntimeException("Access denied");
        }

        serverRepository.delete(server);
        auditService.logAction(userId, "DELETE_SERVER", "Server", serverId.toString(), null);
    }

    public Server regenerateAgentToken(UUID serverId, UUID userId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        if (!teamService.hasTeamAccess(userId, server.getTeam().getId(), TeamMember.TeamRole.ADMIN)) {
            throw new RuntimeException("Access denied");
        }

        server.setAgentToken(generateAgentToken());
        Server updatedServer = serverRepository.save(server);
        
        auditService.logAction(userId, "REGENERATE_AGENT_TOKEN", "Server", serverId.toString(), null);
        
        return updatedServer;
    }

    public Optional<Server> findByAgentToken(String agentToken) {
        return serverRepository.findByAgentToken(agentToken);
    }

    public void updateServerStatus(UUID serverId, Server.Status status) {
        serverRepository.findById(serverId).ifPresent(server -> {
            server.setStatus(status);
            server.setLastSeenAt(LocalDateTime.now());
            serverRepository.save(server);
        });
    }

    public void updateLastSeen(String agentToken) {
        serverRepository.findByAgentToken(agentToken).ifPresent(server -> {
            server.setLastSeenAt(LocalDateTime.now());
            server.setStatus(Server.Status.ONLINE);
            serverRepository.save(server);
        });
    }

    private String generateAgentToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public long getTotalServersCount(UUID userId) {
        List<UUID> teamIds = teamService.getUserTeamIds(userId);
        return serverRepository.countByTeamIdIn(teamIds);
    }

    public long getOnlineServersCount(UUID userId) {
        List<UUID> teamIds = teamService.getUserTeamIds(userId);
        return serverRepository.countOnlineByTeamIdIn(teamIds);
    }
}