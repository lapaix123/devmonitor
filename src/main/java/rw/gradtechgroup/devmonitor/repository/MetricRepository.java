package rw.gradtechgroup.devmonitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rw.gradtechgroup.devmonitor.entity.Metric;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MetricRepository extends JpaRepository<Metric, UUID> {
    
    List<Metric> findByServerIdOrderByTimestampDesc(UUID serverId);
    
    List<Metric> findByServerIdAndMetricTypeOrderByTimestampDesc(UUID serverId, Metric.MetricType metricType);
    
    @Query("SELECT m FROM Metric m WHERE m.server.id = :serverId AND m.timestamp >= :startTime AND m.timestamp <= :endTime ORDER BY m.timestamp DESC")
    List<Metric> findByServerIdAndTimestampBetween(@Param("serverId") UUID serverId, 
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT m FROM Metric m WHERE m.server.id = :serverId AND m.metricType = :metricType AND m.timestamp >= :startTime AND m.timestamp <= :endTime ORDER BY m.timestamp DESC")
    List<Metric> findByServerIdAndMetricTypeAndTimestampBetween(@Param("serverId") UUID serverId,
                                                                @Param("metricType") Metric.MetricType metricType,
                                                                @Param("startTime") LocalDateTime startTime,
                                                                @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT AVG(m.value) FROM Metric m WHERE m.server.id IN :serverIds AND m.metricType = :metricType AND m.timestamp >= :startTime")
    Double findAverageValueByServerIdsAndMetricTypeAndTimestampAfter(@Param("serverIds") List<UUID> serverIds,
                                                                     @Param("metricType") Metric.MetricType metricType,
                                                                     @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT m FROM Metric m WHERE m.server.id = :serverId AND m.metricType = :metricType ORDER BY m.timestamp DESC LIMIT 1")
    Metric findLatestByServerIdAndMetricType(@Param("serverId") UUID serverId, @Param("metricType") Metric.MetricType metricType);
}