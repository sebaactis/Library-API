package com.library.administration.services.implementation;

import com.library.administration.models.entities.Author;
import com.library.administration.models.entities.Book;
import com.library.administration.repositories.AuthorRepository;
import com.library.administration.repositories.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService extends ServiceImple<Book, Long> {

    private final AuthorRepository authorRepository;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        super(bookRepository);
        this.authorRepository = authorRepository;
    }

    public Book createBookWithAuthor(Book bookRequest, Long authorId) {
        Author author = authorRepository.findById(authorId).orElse(null);

        if(author == null) return null;

        bookRequest.setAuthor(author);

        return save(bookRequest);
    }

    public Book changeAuthor(Long bookId, Long authorId) {
        Author author = authorRepository.findById(authorId).orElse(null);

        if(author == null) return null;

        Book book = findById(bookId);

        if(book == null) return null;

        book.setAuthor(author);

        return save(book);
    }
}
