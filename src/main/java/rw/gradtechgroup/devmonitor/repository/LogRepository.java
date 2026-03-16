package rw.gradtechgroup.devmonitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rw.gradtechgroup.devmonitor.entity.Log;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LogRepository extends JpaRepository<Log, UUID> {
    
    List<Log> findByServerIdOrderByTimestampDesc(UUID serverId);
    
    List<Log> findByServerIdAndLogTypeOrderByTimestampDesc(UUID serverId, Log.LogType logType);
    
    List<Log> findByServerIdAndLevelOrderByTimestampDesc(UUID serverId, Log.LogLevel level);
    
    @Query("SELECT l FROM Log l WHERE l.server.id = :serverId AND l.timestamp >= :startTime AND l.timestamp <= :endTime ORDER BY l.timestamp DESC")
    List<Log> findByServerIdAndTimestampBetween(@Param("serverId") UUID serverId,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);
}