package com.library.administration.services;

import com.library.administration.models.entities.Notification;
import com.library.administration.models.entities.User;
import com.library.administration.repositories.NotificationRepository;
import com.library.administration.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void createNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification(user, message);
        notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByUserIdAndIsReadFalse(user.getId());
    }
}
