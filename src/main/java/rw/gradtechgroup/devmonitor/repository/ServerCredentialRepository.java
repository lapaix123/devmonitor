package rw.gradtechgroup.devmonitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.gradtechgroup.devmonitor.entity.ServerCredential;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServerCredentialRepository extends JpaRepository<ServerCredential, UUID> {
    
    List<ServerCredential> findByServerId(UUID serverId);
    
    List<ServerCredential> findByServerIdAndType(UUID serverId, ServerCredential.CredentialType type);
}