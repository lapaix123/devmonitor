package rw.gradtechgroup.devmonitor.service;

import rw.gradtechgroup.devmonitor.entity.AlertEvent;
import rw.gradtechgroup.devmonitor.entity.AlertRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertService {
    AlertRule createAlertRule(UUID serverId, AlertRule.Condition condition, 
                             Double threshold, Integer durationSeconds, UUID userId);
    List<AlertRule> getAlertRules(UUID userId);
    AlertRule updateAlertRule(UUID ruleId, AlertRule.Condition condition, 
                             Double threshold, Integer durationSeconds, 
                             Boolean isActive, UUID userId);
    void deleteAlertRule(UUID ruleId, UUID userId);
    List<AlertEvent> getAlertEvents(UUID userId);
    Optional<AlertEvent> getAlertEvent(UUID eventId, UUID userId);
    long getActiveAlertsCount(UUID userId);
    void evaluateAlerts();
}