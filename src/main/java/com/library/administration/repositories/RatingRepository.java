package com.library.administration.repositories;

import com.library.administration.models.entities.Book;
import com.library.administration.models.entities.Rating;
import com.library.administration.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Rating findByUserAndBook(User user, Book book);

    List<Rating> findByBook(Book book);
}
