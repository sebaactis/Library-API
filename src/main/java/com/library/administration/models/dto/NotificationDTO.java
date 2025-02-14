package com.library.administration.models.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private UserDTO user;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}
