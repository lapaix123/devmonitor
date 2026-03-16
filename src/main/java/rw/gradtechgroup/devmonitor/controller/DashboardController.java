package rw.gradtechgroup.devmonitor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.gradtechgroup.devmonitor.dto.DashboardSummaryResponse;
import rw.gradtechgroup.devmonitor.entity.Metric;
import rw.gradtechgroup.devmonitor.security.UserPrincipal;
import rw.gradtechgroup.devmonitor.service.AlertService;
import rw.gradtechgroup.devmonitor.service.MetricService;
import rw.gradtechgroup.devmonitor.service.ServerService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    @Autowired
    private ServerService serverService;

    @Autowired
    private MetricService metricService;

    @Autowired
    private AlertService alertService;

    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            long totalServers = serverService.getTotalServersCount(userPrincipal.getId());
            long onlineServers = serverService.getOnlineServersCount(userPrincipal.getId());
            long alertsCount = alertService.getActiveAlertsCount(userPrincipal.getId());
            
            // Get average metrics for the last hour
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            Double avgCpu = metricService.getAverageMetricValue(userPrincipal.getId(), Metric.MetricType.CPU, oneHourAgo);
            Double avgRam = metricService.getAverageMetricValue(userPrincipal.getId(), Metric.MetricType.RAM, oneHourAgo);
            
            DashboardSummaryResponse response = new DashboardSummaryResponse(
                    totalServers,
                    onlineServers,
                    alertsCount,
                    avgCpu != null ? avgCpu : 0.0,
                    avgRam != null ? avgRam : 0.0
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/servers-health")
    public ResponseEntity<?> getServersHealth(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            // This would return detailed health information for all servers
            // Implementation depends on specific requirements
            return ResponseEntity.ok().body("Servers health endpoint - to be implemented");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}