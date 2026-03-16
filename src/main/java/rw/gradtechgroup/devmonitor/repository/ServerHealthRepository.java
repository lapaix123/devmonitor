package rw.gradtechgroup.devmonitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rw.gradtechgroup.devmonitor.entity.ServerHealth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServerHealthRepository extends JpaRepository<ServerHealth, UUID> {
    
    Optional<ServerHealth> findByServerId(UUID serverId);
    
    @Query("SELECT sh FROM ServerHealth sh WHERE sh.server.id IN :serverIds")
    List<ServerHealth> findByServerIdIn(@Param("serverIds") List<UUID> serverIds);
    
    @Query("SELECT AVG(sh.cpuUsage) FROM ServerHealth sh WHERE sh.server.id IN :serverIds")
    Double findAverageCpuUsageByServerIdIn(@Param("serverIds") List<UUID> serverIds);
    
    @Query("SELECT AVG(sh.ramUsage) FROM ServerHealth sh WHERE sh.server.id IN :serverIds")
    Double findAverageRamUsageByServerIdIn(@Param("serverIds") List<UUID> serverIds);
}