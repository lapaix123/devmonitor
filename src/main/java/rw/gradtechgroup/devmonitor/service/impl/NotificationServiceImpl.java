package rw.gradtechgroup.devmonitor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gradtechgroup.devmonitor.entity.Notification;
import rw.gradtechgroup.devmonitor.entity.User;
import rw.gradtechgroup.devmonitor.repository.NotificationRepository;
import rw.gradtechgroup.devmonitor.repository.UserRepository;
import rw.gradtechgroup.devmonitor.service.NotificationService;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        // For now, we'll just delete the notification to mark as "read"
        // In a full implementation, you might add a "read" status field
        notificationRepository.delete(notification);
    }

    @Override
    public Notification createNotification(UUID userId, Notification.NotificationType type, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setMessage(message);
        notification.setStatus(Notification.NotificationStatus.PENDING);

        return notificationRepository.save(notification);
    }
}