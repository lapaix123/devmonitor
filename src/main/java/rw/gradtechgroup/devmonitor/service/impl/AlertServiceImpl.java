package rw.gradtechgroup.devmonitor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gradtechgroup.devmonitor.entity.AlertEvent;
import rw.gradtechgroup.devmonitor.entity.AlertRule;
import rw.gradtechgroup.devmonitor.entity.Server;
import rw.gradtechgroup.devmonitor.entity.TeamMember;
import rw.gradtechgroup.devmonitor.repository.AlertEventRepository;
import rw.gradtechgroup.devmonitor.repository.AlertRuleRepository;
import rw.gradtechgroup.devmonitor.service.AlertService;
import rw.gradtechgroup.devmonitor.service.AuditService;
import rw.gradtechgroup.devmonitor.service.ServerService;
import rw.gradtechgroup.devmonitor.service.TeamService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertRuleRepository alertRuleRepository;

    @Autowired
    private AlertEventRepository alertEventRepository;

    @Autowired
    private ServerService serverService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private AuditService auditService;

    public AlertRule createAlertRule(UUID serverId, AlertRule.Condition condition, 
                                    Double threshold, Integer durationSeconds, UUID userId) {
        Server server = serverService.findById(serverId, userId)
                .orElseThrow(() -> new RuntimeException("Server not found or access denied"));

        AlertRule alertRule = new AlertRule();
        alertRule.setServer(server);
        alertRule.setCondition(condition);
        alertRule.setThreshold(threshold);
        alertRule.setDurationSeconds(durationSeconds);
        alertRule.setIsActive(true);

        AlertRule savedRule = alertRuleRepository.save(alertRule);
        auditService.logAction(userId, "CREATE_ALERT_RULE", "AlertRule", savedRule.getId().toString(), null);
        
        return savedRule;
    }

    public List<AlertRule> getAlertRules(UUID userId) {
        List<UUID> teamIds = teamService.getUserTeamIds(userId);
        List<UUID> serverIds = serverService.getUserServers(userId).stream()
                .map(Server::getId)
                .toList();
        
        return alertRuleRepository.findActiveByServerIdIn(serverIds);
    }

    public AlertRule updateAlertRule(UUID ruleId, AlertRule.Condition condition, 
                                    Double threshold, Integer durationSeconds, 
                                    Boolean isActive, UUID userId) {
        AlertRule alertRule = alertRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Alert rule not found"));

        // Check access through server
        if (!teamService.hasTeamAccess(userId, alertRule.getServer().getTeam().getId(), TeamMember.TeamRole.MEMBER)) {
            throw new RuntimeException("Access denied");
        }

        alertRule.setCondition(condition);
        alertRule.setThreshold(threshold);
        alertRule.setDurationSeconds(durationSeconds);
        alertRule.setIsActive(isActive);

        AlertRule updatedRule = alertRuleRepository.save(alertRule);
        auditService.logAction(userId, "UPDATE_ALERT_RULE", "AlertRule", ruleId.toString(), null);
        
        return updatedRule;
    }

    public void deleteAlertRule(UUID ruleId, UUID userId) {
        AlertRule alertRule = alertRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Alert rule not found"));

        if (!teamService.hasTeamAccess(userId, alertRule.getServer().getTeam().getId(), TeamMember.TeamRole.ADMIN)) {
            throw new RuntimeException("Access denied");
        }

        alertRuleRepository.delete(alertRule);
        auditService.logAction(userId, "DELETE_ALERT_RULE", "AlertRule", ruleId.toString(), null);
    }

    public List<AlertEvent> getAlertEvents(UUID userId) {
        List<UUID> serverIds = serverService.getUserServers(userId).stream()
                .map(Server::getId)
                .toList();
        
        return alertEventRepository.findByServerIdInOrderByTriggeredAtDesc(serverIds);
    }

    public Optional<AlertEvent> getAlertEvent(UUID eventId, UUID userId) {
        Optional<AlertEvent> event = alertEventRepository.findById(eventId);
        if (event.isPresent()) {
            if (!teamService.hasTeamAccess(userId, event.get().getServer().getTeam().getId(), TeamMember.TeamRole.MEMBER)) {
                return Optional.empty();
            }
        }
        return event;
    }

    public long getActiveAlertsCount(UUID userId) {
        List<UUID> serverIds = serverService.getUserServers(userId).stream()
                .map(Server::getId)
                .toList();
        
        return alertEventRepository.countActiveAlertsByServerIdIn(serverIds);
    }

    // This method would be called by a background job to evaluate alert conditions
    public void evaluateAlerts() {
        // Implementation for alert evaluation logic
        // This would check current metrics against alert rules and create AlertEvents
    }
}