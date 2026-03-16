package rw.gradtechgroup.devmonitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rw.gradtechgroup.devmonitor.entity.AlertEvent;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertEventRepository extends JpaRepository<AlertEvent, UUID> {
    
    List<AlertEvent> findByServerIdOrderByTriggeredAtDesc(UUID serverId);
    
    @Query("SELECT ae FROM AlertEvent ae WHERE ae.server.id IN :serverIds ORDER BY ae.triggeredAt DESC")
    List<AlertEvent> findByServerIdInOrderByTriggeredAtDesc(@Param("serverIds") List<UUID> serverIds);
    
    List<AlertEvent> findByAlertRuleIdOrderByTriggeredAtDesc(UUID alertRuleId);
    
    @Query("SELECT COUNT(ae) FROM AlertEvent ae WHERE ae.server.id IN :serverIds AND ae.status = 'TRIGGERED'")
    long countActiveAlertsByServerIdIn(@Param("serverIds") List<UUID> serverIds);
}