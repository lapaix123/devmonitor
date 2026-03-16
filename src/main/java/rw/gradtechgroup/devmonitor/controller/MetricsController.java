package rw.gradtechgroup.devmonitor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.gradtechgroup.devmonitor.dto.MetricsRequest;
import rw.gradtechgroup.devmonitor.entity.Metric;
import rw.gradtechgroup.devmonitor.security.UserPrincipal;
import rw.gradtechgroup.devmonitor.service.MetricService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MetricsController {

    @Autowired
    private MetricService metricService;

    @PostMapping("/metrics/ingest")
    public ResponseEntity<?> ingestMetrics(@RequestBody MetricsRequest request) {
        try {
            List<MetricService.MetricData> metrics = request.getMetrics().stream()
                    .map(m -> new MetricService.MetricData(
                            Metric.MetricType.valueOf(m.getType().toUpperCase()),
                            m.getValue()
                    ))
                    .collect(Collectors.toList());

            metricService.ingestMetrics(request.getAgentToken(), metrics);
            
            return ResponseEntity.ok().body("Metrics ingested successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/servers/{id}/metrics")
    public ResponseEntity<?> getServerMetrics(
            @PathVariable UUID id,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Authentication authentication) {
        
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            List<Metric> metrics;
            
            if (from != null && to != null) {
                Metric.MetricType metricType = type != null ? Metric.MetricType.valueOf(type.toUpperCase()) : null;
                metrics = metricService.getServerMetricsInRange(id, metricType, from, to, userPrincipal.getId());
            } else if (type != null) {
                Metric.MetricType metricType = Metric.MetricType.valueOf(type.toUpperCase());
                metrics = metricService.getServerMetricsByType(id, metricType, userPrincipal.getId());
            } else {
                metrics = metricService.getServerMetrics(id, userPrincipal.getId());
            }
            
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}