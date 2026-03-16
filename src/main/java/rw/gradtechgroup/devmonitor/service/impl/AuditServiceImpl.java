package rw.gradtechgroup.devmonitor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gradtechgroup.devmonitor.entity.AuditLog;
import rw.gradtechgroup.devmonitor.entity.User;
import rw.gradtechgroup.devmonitor.repository.AuditLogRepository;
import rw.gradtechgroup.devmonitor.repository.UserRepository;
import rw.gradtechgroup.devmonitor.service.AuditService;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    public void logAction(UUID userId, String action, String entityType, String entityId, String metadata) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setMetadata(metadata);

        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAuditLogsByUser(UUID userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<AuditLog> getAuditLogsByEntity(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }
}