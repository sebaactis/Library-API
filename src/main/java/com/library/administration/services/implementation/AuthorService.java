package com.library.administration.services.implementation;

import com.library.administration.models.entities.Author;
import com.library.administration.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorService extends ServiceImple<Author, Long> {

    public AuthorService(AuthorRepository authorRepository) {
        super(authorRepository);
    }
}
