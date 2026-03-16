package rw.gradtechgroup.devmonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import rw.gradtechgroup.devmonitor.entity.Log;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogIngestRequest {
    private String agentToken;
    private List<LogData> logs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogData {
        private Log.LogType logType;
        private String message;
        private Log.LogLevel level;
    }
}