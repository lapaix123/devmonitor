package rw.gradtechgroup.devmonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsRequest {
    private String agentToken;
    private List<MetricData> metrics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricData {
        private String type;
        private Double value;
    }
}