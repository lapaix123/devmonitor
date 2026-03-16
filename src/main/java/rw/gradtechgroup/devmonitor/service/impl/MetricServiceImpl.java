package rw.gradtechgroup.devmonitor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gradtechgroup.devmonitor.entity.Metric;
import rw.gradtechgroup.devmonitor.entity.Server;
import rw.gradtechgroup.devmonitor.entity.ServerHealth;
import rw.gradtechgroup.devmonitor.entity.TeamMember;
import rw.gradtechgroup.devmonitor.repository.MetricRepository;
import rw.gradtechgroup.devmonitor.repository.ServerHealthRepository;
import rw.gradtechgroup.devmonitor.service.MetricService;
import rw.gradtechgroup.devmonitor.service.ServerService;
import rw.gradtechgroup.devmonitor.service.TeamService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MetricServiceImpl implements MetricService {

    @Autowired
    private MetricRepository metricRepository;

    @Autowired
    private ServerHealthRepository serverHealthRepository;

    @Autowired
    private ServerService serverService;

    @Autowired
    private TeamService teamService;

    @Override
    public void ingestMetrics(String agentToken, List<MetricService.MetricData> metrics) {
        Server server = serverService.findByAgentToken(agentToken)
                .orElseThrow(() -> new RuntimeException("Invalid agent token"));

        // Update server last seen
        serverService.updateLastSeen(agentToken);

        // Save metrics
        for (MetricService.MetricData metricData : metrics) {
            Metric metric = new Metric();
            metric.setServer(server);
            metric.setMetricType(metricData.getType());
            metric.setValue(metricData.getValue());
            metric.setTimestamp(LocalDateTime.now());
            
            metricRepository.save(metric);
        }

        // Update server health summary
        updateServerHealth(server, metrics);
    }

    private void updateServerHealth(Server server, List<MetricService.MetricData> metrics) {
        ServerHealth health = serverHealthRepository.findByServerId(server.getId())
                .orElse(new ServerHealth());

        if (health.getServer() == null) {
            health.setServer(server);
        }

        for (MetricService.MetricData metricData : metrics) {
            switch (metricData.getType()) {
                case CPU:
                    health.setCpuUsage(metricData.getValue());
                    break;
                case RAM:
                    health.setRamUsage(metricData.getValue());
                    break;
                case DISK:
                    health.setDiskUsage(metricData.getValue());
                    break;
            }
        }

        serverHealthRepository.save(health);
    }

    public List<Metric> getServerMetrics(UUID serverId, UUID userId) {
        Server server = serverService.findById(serverId, userId)
                .orElseThrow(() -> new RuntimeException("Server not found or access denied"));

        return metricRepository.findByServerIdOrderByTimestampDesc(serverId);
    }

    public List<Metric> getServerMetricsByType(UUID serverId, Metric.MetricType type, UUID userId) {
        Server server = serverService.findById(serverId, userId)
                .orElseThrow(() -> new RuntimeException("Server not found or access denied"));

        return metricRepository.findByServerIdAndMetricTypeOrderByTimestampDesc(serverId, type);
    }

    public List<Metric> getServerMetricsInRange(UUID serverId, Metric.MetricType type, 
                                                LocalDateTime from, LocalDateTime to, UUID userId) {
        Server server = serverService.findById(serverId, userId)
                .orElseThrow(() -> new RuntimeException("Server not found or access denied"));

        if (type != null) {
            return metricRepository.findByServerIdAndMetricTypeAndTimestampBetween(serverId, type, from, to);
        } else {
            return metricRepository.findByServerIdAndTimestampBetween(serverId, from, to);
        }
    }

    public Double getAverageMetricValue(UUID userId, Metric.MetricType metricType, LocalDateTime since) {
        List<UUID> teamIds = teamService.getUserTeamIds(userId);
        List<UUID> serverIds = serverService.getUserServers(userId).stream()
                .map(Server::getId)
                .toList();

        return metricRepository.findAverageValueByServerIdsAndMetricTypeAndTimestampAfter(
                serverIds, metricType, since);
    }


}