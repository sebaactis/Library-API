package com.library.administration.repositories;

import com.library.administration.models.entities.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByUserId(Long userId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    WishList findByUserIdAndBookId(Long userId, Long bookId);
}
