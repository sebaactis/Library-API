package com.library.administration.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private Integer score;
    private LocalDateTime createdAt;
}
