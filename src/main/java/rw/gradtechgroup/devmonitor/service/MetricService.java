package rw.gradtechgroup.devmonitor.service;

import rw.gradtechgroup.devmonitor.entity.Metric;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MetricService {
    void ingestMetrics(String agentToken, List<MetricData> metrics);
    List<Metric> getServerMetrics(UUID serverId, UUID userId);
    List<Metric> getServerMetricsByType(UUID serverId, Metric.MetricType type, UUID userId);
    List<Metric> getServerMetricsInRange(UUID serverId, Metric.MetricType type, 
                                        LocalDateTime from, LocalDateTime to, UUID userId);
    Double getAverageMetricValue(UUID userId, Metric.MetricType metricType, LocalDateTime since);

    class MetricData {
        private Metric.MetricType type;
        private Double value;

        public MetricData() {}

        public MetricData(Metric.MetricType type, Double value) {
            this.type = type;
            this.value = value;
        }

        public Metric.MetricType getType() { return type; }
        public void setType(Metric.MetricType type) { this.type = type; }
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }
}