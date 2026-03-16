package rw.gradtechgroup.devmonitor.service;

import rw.gradtechgroup.devmonitor.entity.Server;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServerService {
    Server createServer(UUID teamId, String name, String ipAddress, Integer port, String os, 
                       Server.Environment environment, UUID userId);
    List<Server> getUserServers(UUID userId);
    Optional<Server> findById(UUID id, UUID userId);
    Server updateServer(UUID serverId, String name, String ipAddress, Integer port, String os, 
                       Server.Environment environment, UUID userId);
    void deleteServer(UUID serverId, UUID userId);
    Server regenerateAgentToken(UUID serverId, UUID userId);
    Optional<Server> findByAgentToken(String agentToken);
    void updateServerStatus(UUID serverId, Server.Status status);
    void updateLastSeen(String agentToken);
    long getTotalServersCount(UUID userId);
    long getOnlineServersCount(UUID userId);
}