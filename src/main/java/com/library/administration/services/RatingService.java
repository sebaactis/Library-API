package com.library.administration.services;

import com.library.administration.models.entities.Book;
import com.library.administration.models.entities.Rating;
import com.library.administration.models.entities.User;
import com.library.administration.repositories.BookRepository;
import com.library.administration.repositories.RatingRepository;
import com.library.administration.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public Rating addOrUpdateRating(Long userId, Long bookId, Integer score) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Rating existingRating = ratingRepository.findByUserAndBook(user, book);

        if (existingRating != null) {
            existingRating.setScore(score);
            return ratingRepository.save(existingRating);
        } else {
            Rating newRating = new Rating(user, book, score);
            return ratingRepository.save(newRating);
        }
    }

    public Double getAverageRatingForBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        return book.getAverageRating();
    }

    public Map<String, Object> getMappingAverageRatingForBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Double averageRating = book.getAverageRating();
        Map<String, Object> result = new HashMap<>();
        result.put("averageRating", averageRating);
        result.put("bookName", book.getTitle());

        return result;
    }
}
