package com.library.administration.controllers;

import com.library.administration.models.dto.RatingDTO;
import com.library.administration.models.entities.Rating;
import com.library.administration.services.RatingService;
import com.library.administration.utilities.ApiResponse;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @PostMapping("/{userId}/{bookId}")
    public ResponseEntity<ApiResponse<RatingDTO>> addOrUpdateRating(
            @PathVariable Long userId,
            @PathVariable Long bookId,
            @RequestParam Integer score) {
        try {
            if (score < 1 || score > 5) {
                ApiResponse<RatingDTO> response = new ApiResponse<>("The score cannot be less than 1 or more than 5",
                        null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Rating rating = ratingService.addOrUpdateRating(userId, bookId, score);

            if (rating == null) {
                ApiResponse<RatingDTO> response = new ApiResponse<>("Rating add failed! Try again", null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();
            RatingDTO ratingDTO = modelMapper.map(rating, RatingDTO.class);

            ApiResponse<RatingDTO> response = new ApiResponse<>("Rating add successfully", ratingDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ApiResponse<RatingDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("average/{bookId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAverageRating(@PathVariable Long bookId) {
        try {
            Map<String, Object> ratingData = ratingService.getMappingAverageRatingForBook(bookId);

            ApiResponse<Map<String, Object>> response = new ApiResponse<>("Rating data retrieved successfully",
                    ratingData);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
