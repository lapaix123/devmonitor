package rw.gradtechgroup.devmonitor.service;

import rw.gradtechgroup.devmonitor.entity.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditService {
    void logAction(UUID userId, String action, String entityType, String entityId, String metadata);
    List<AuditLog> getAuditLogsByUser(UUID userId);
    List<AuditLog> getAuditLogsByEntity(String entityType, String entityId);
}