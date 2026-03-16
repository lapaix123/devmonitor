package rw.gradtechgroup.devmonitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rw.gradtechgroup.devmonitor.entity.TeamMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    
    List<TeamMember> findByTeamId(UUID teamId);
    
    List<TeamMember> findByUserId(UUID userId);
    
    Optional<TeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId);
    
    @Query("SELECT tm FROM TeamMember tm WHERE tm.user.id = :userId AND tm.team.id IN :teamIds")
    List<TeamMember> findByUserIdAndTeamIdIn(@Param("userId") UUID userId, @Param("teamIds") List<UUID> teamIds);
    
    boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);
}