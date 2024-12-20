package com.library.administration.services.implementation;

import com.library.administration.models.entities.Book;
import com.library.administration.repositories.BookRepository;

import org.springframework.stereotype.Service;

@Service
public class BookService extends ServiceImple<Book, Long> {

    public BookService(BookRepository bookRepository) {
        super(bookRepository);
    }
}
