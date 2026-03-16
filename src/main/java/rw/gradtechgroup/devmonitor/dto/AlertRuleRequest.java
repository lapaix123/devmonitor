package rw.gradtechgroup.devmonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import rw.gradtechgroup.devmonitor.entity.AlertRule;
import rw.gradtechgroup.devmonitor.entity.Metric;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertRuleRequest {
    private UUID serverId;
    private Metric.MetricType metricType;
    private AlertRule.Condition condition;
    private Double threshold;
    private Integer durationSeconds;
    private Boolean isActive = true;
}