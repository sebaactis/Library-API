package com.library.administration.services;

import com.library.administration.models.entities.Book;
import com.library.administration.models.entities.User;
import com.library.administration.models.entities.WishList;
import com.library.administration.repositories.BookRepository;
import com.library.administration.repositories.UserRepository;
import com.library.administration.repositories.WishListRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WishListService {

    private final WishListRepository wishListRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public WishListService(WishListRepository wishListRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.wishListRepository = wishListRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public WishList addBookToWishList(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (wishListRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new RuntimeException("The book is already in the wish list");
        }

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD");
        String formattedDate = formatter.format(date);

        WishList wishListEntry = new WishList(user, book);
        return wishListRepository.save(wishListEntry);
    }

    public List<WishList> getWishListByUser(Long userId) {
        return wishListRepository.findByUserId(userId);
    }

    public void removeBookFromWishList(Long userId, Long bookId) {
        WishList wishListEntry = wishListRepository.findByUserIdAndBookId(userId, bookId);

        if(wishListEntry == null) {
            throw new RuntimeException("Wish List entry not found");
        }

        wishListRepository.delete(wishListEntry);
    }
}
