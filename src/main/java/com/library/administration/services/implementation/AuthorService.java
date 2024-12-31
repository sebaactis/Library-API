package com.library.administration.services.implementation;

import com.library.administration.models.dto.AuthorDTO;
import com.library.administration.models.dto.BookDTO;
import com.library.administration.models.entities.Author;
import com.library.administration.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService extends ServiceImple<Author, Long> {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        super(authorRepository);
        this.authorRepository = authorRepository;
    }

    public List<AuthorDTO> findAllAuthors() {
        return authorRepository.findAll().stream()
                .map(author -> new AuthorDTO(
                        author.getName(),
                        author.getBirthDate(),
                        author.getBooks().stream()
                                .map(book -> new BookDTO(book.getId(), book.getTitle(), book.getGenre(), book.getPublished()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}
