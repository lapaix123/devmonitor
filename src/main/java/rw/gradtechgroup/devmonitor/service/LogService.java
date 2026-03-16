package rw.gradtechgroup.devmonitor.service;

import rw.gradtechgroup.devmonitor.dto.LogIngestRequest;
import rw.gradtechgroup.devmonitor.entity.Log;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LogService {
    void ingestLogs(String agentToken, List<LogIngestRequest.LogData> logs);
    List<Log> getServerLogs(UUID serverId, UUID userId);
    List<Log> getServerLogsByType(UUID serverId, Log.LogType logType, UUID userId);
    List<Log> getServerLogsByLevel(UUID serverId, Log.LogLevel level, UUID userId);
    List<Log> getServerLogsInRange(UUID serverId, LocalDateTime from, LocalDateTime to, UUID userId);
}