package rw.gradtechgroup.devmonitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rw.gradtechgroup.devmonitor.entity.AlertRule;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, UUID> {
    
    List<AlertRule> findByServerIdAndIsActiveTrue(UUID serverId);
    
    @Query("SELECT ar FROM AlertRule ar WHERE ar.server.id IN :serverIds AND ar.isActive = true")
    List<AlertRule> findActiveByServerIdIn(@Param("serverIds") List<UUID> serverIds);
    
    List<AlertRule> findByServerId(UUID serverId);
}