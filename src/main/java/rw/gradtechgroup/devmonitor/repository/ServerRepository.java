package rw.gradtechgroup.devmonitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rw.gradtechgroup.devmonitor.entity.Server;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServerRepository extends JpaRepository<Server, UUID> {
    
    List<Server> findByTeamId(UUID teamId);
    
    @Query("SELECT s FROM Server s WHERE s.team.id IN :teamIds")
    List<Server> findByTeamIdIn(@Param("teamIds") List<UUID> teamIds);
    
    Optional<Server> findByAgentToken(String agentToken);
    
    boolean existsByAgentToken(String agentToken);
    
    @Query("SELECT COUNT(s) FROM Server s WHERE s.team.id IN :teamIds")
    long countByTeamIdIn(@Param("teamIds") List<UUID> teamIds);
    
    @Query("SELECT COUNT(s) FROM Server s WHERE s.team.id IN :teamIds AND s.status = 'ONLINE'")
    long countOnlineByTeamIdIn(@Param("teamIds") List<UUID> teamIds);
}