package com.library.administration.controllers;

import com.library.administration.models.dto.NotificationDTO;
import com.library.administration.models.entities.Notification;
import com.library.administration.services.NotificationService;
import com.library.administration.utilities.ApiResponse;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications(Authentication authentication) {
        try {

            String userEmail = authentication.getName();

            List<Notification> notifications = notificationService.getUnreadNotifications(userEmail);

            if (notifications == null) {
                ApiResponse<List<NotificationDTO>> response = new ApiResponse<>("The current user does not have any unread notifications", null);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();

            List<NotificationDTO> notificationsDTO = notifications.stream()
                    .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                    .toList();

            ApiResponse<List<NotificationDTO>> response = new ApiResponse<>("Notifications found", notificationsDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<List<NotificationDTO>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/{notificationId}/markAsRead")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);

            ApiResponse<String> response = new ApiResponse<>("Notification mark as read", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
