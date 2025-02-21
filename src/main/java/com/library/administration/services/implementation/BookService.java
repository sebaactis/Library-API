package com.library.administration.services.implementation;

import com.library.administration.models.dto.BookDTO;
import com.library.administration.models.entities.Author;
import com.library.administration.models.entities.Book;
import com.library.administration.repositories.AuthorRepository;
import com.library.administration.repositories.BookRepository;
import com.library.administration.services.RatingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BookService extends ServiceImple<Book, Long> {

    private final AuthorRepository authorRepository;
    private final RatingService ratingService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, RatingService ratingService) {
        super(bookRepository);
        this.authorRepository = authorRepository;
        this.ratingService = ratingService;
    }

    public Page<BookDTO> findAll(int page, int size, String sort, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Book> booksPage = ((BookRepository) repository).findAll(pageable);

        return booksPage.map(book -> {
            Double averageRating = ratingService.getAverageRatingForBook(book.getId());
            return new BookDTO(
                    book.getId(),
                    book.getTitle(),
                    book.getGenre(),
                    book.getPublished(),
                    averageRating
            );
        });
    }

    public Book createBookWithAuthor(Book bookRequest, Long authorId) {
        Author author = authorRepository.findById(authorId).orElse(null);

        if (author == null) return null;

        bookRequest.setAuthor(author);

        return save(bookRequest);
    }

    public Book changeAuthor(Long bookId, Long authorId) {
        Author author = authorRepository.findById(authorId).orElse(null);

        if (author == null) return null;

        Book book = findById(bookId);

        if (book == null) return null;

        book.setAuthor(author);

        return save(book);
    }
}
