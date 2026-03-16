package rw.gradtechgroup.devmonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private Long totalServers;
    private Long onlineServers;
    private Long alertsCount;
    private Double avgCpu;
    private Double avgRam;
}