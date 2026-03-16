package rw.gradtechgroup.devmonitor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gradtechgroup.devmonitor.dto.LogIngestRequest;
import rw.gradtechgroup.devmonitor.entity.Log;
import rw.gradtechgroup.devmonitor.entity.Server;
import rw.gradtechgroup.devmonitor.repository.LogRepository;
import rw.gradtechgroup.devmonitor.service.LogService;
import rw.gradtechgroup.devmonitor.service.ServerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ServerService serverService;

    @Override
    public void ingestLogs(String agentToken, List<LogIngestRequest.LogData> logs) {
        Server server = serverService.findByAgentToken(agentToken)
                .orElseThrow(() -> new RuntimeException("Invalid agent token"));

        serverService.updateLastSeen(agentToken);

        for (LogIngestRequest.LogData logData : logs) {
            Log log = new Log();
            log.setServer(server);
            log.setLogType(logData.getLogType());
            log.setMessage(logData.getMessage());
            log.setLevel(logData.getLevel());
            log.setTimestamp(LocalDateTime.now());
            
            logRepository.save(log);
        }
    }

    @Override
    public List<Log> getServerLogs(UUID serverId, UUID userId) {
        Server server = serverService.findById(serverId, userId)
                .orElseThrow(() -> new RuntimeException("Server not found or access denied"));

        return logRepository.findByServerIdOrderByTimestampDesc(serverId);
    }

    @Override
    public List<Log> getServerLogsByType(UUID serverId, Log.LogType logType, UUID userId) {
        Server server = serverService.findById(serverId, userId)
                .orElseThrow(() -> new RuntimeException("Server not found or access denied"));

        return logRepository.findByServerIdAndLogTypeOrderByTimestampDesc(serverId, logType);
    }

    @Override
    public List<Log> getServerLogsByLevel(UUID serverId, Log.LogLevel level, UUID userId) {
        Server server = serverService.findById(serverId, userId)
                .orElseThrow(() -> new RuntimeException("Server not found or access denied"));

        return logRepository.findByServerIdAndLevelOrderByTimestampDesc(serverId, level);
    }

    @Override
    public List<Log> getServerLogsInRange(UUID serverId, LocalDateTime from, LocalDateTime to, UUID userId) {
        Server server = serverService.findById(serverId, userId)
                .orElseThrow(() -> new RuntimeException("Server not found or access denied"));

        return logRepository.findByServerIdAndTimestampBetween(serverId, from, to);
    }
}