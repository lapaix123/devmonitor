package rw.gradtechgroup.devmonitor.service;

import rw.gradtechgroup.devmonitor.entity.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<Notification> getUserNotifications(UUID userId);
    void markAsRead(UUID notificationId, UUID userId);
    Notification createNotification(UUID userId, Notification.NotificationType type, String message);
}