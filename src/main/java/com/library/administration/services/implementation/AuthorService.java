package com.library.administration.services.implementation;

import com.library.administration.models.dto.AuthorDTO;
import com.library.administration.models.dto.BookDTO;
import com.library.administration.models.entities.Author;
import com.library.administration.repositories.AuthorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<AuthorDTO> findAllAuthors(int page, int size, String sort, String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));


        Page<Author> authorsPage = authorRepository.findAll(pageable);

        return authorsPage.map(author -> new AuthorDTO(
                author.getId(),
                author.getName(),
                author.getBirthDate(),
                author.getBooks().stream()
                        .map(book -> new BookDTO(
                                book.getId(),
                                book.getTitle(),
                                book.getGenre(),
                                book.getPublished(),
                                book.getAverageRating()
                        ))
                        .collect(Collectors.toList())
        ));
    }
}
