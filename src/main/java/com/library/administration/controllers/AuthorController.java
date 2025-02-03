package com.library.administration.controllers;

import com.library.administration.models.dti.AuthorDTI;
import com.library.administration.models.dto.AuthorDTO;
import com.library.administration.models.entities.Author;
import com.library.administration.services.implementation.AuthorService;
import com.library.administration.utilities.ApiResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping("authors")
    public ResponseEntity<ApiResponse<List<AuthorDTO>>> findAll() {

        try {
            List<AuthorDTO> authors = authorService.findAllAuthors();

            if (authors.isEmpty()) {
                ApiResponse<List<AuthorDTO>> response = new ApiResponse<>("We don´t have authors yet", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ApiResponse<List<AuthorDTO>> response = new ApiResponse<>("Get authors successfully", authors);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<List<AuthorDTO>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("author/{id}")
    public ResponseEntity<ApiResponse<AuthorDTO>> findById(@PathVariable Long id) {

        try {
            Author author = authorService.findById(id);

            if (author == null) {
                ApiResponse<AuthorDTO> response = new ApiResponse<>("Author not found, please try with another", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();
            AuthorDTO authorDTO = modelMapper.map(author, AuthorDTO.class);

            ApiResponse<AuthorDTO> response = new ApiResponse<>("Author found!", authorDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<AuthorDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("author")
    public ResponseEntity<ApiResponse<Author>> create(@Valid @RequestBody AuthorDTI authorRequest) {
        try {
            Author author = new Author();
            author.setName(authorRequest.getName());
            author.setBirthDate(authorRequest.getBirthDate());

            Author savedAuthor = authorService.save(author);

            if (savedAuthor == null) {
                throw new IllegalStateException("Failed to save the author");
            }

            ApiResponse<Author> response = new ApiResponse<>("Author created successfully", savedAuthor);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            ApiResponse<Author> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("author/{id}")
    public ResponseEntity<ApiResponse<AuthorDTO>> update(@PathVariable Long id, @Valid @RequestBody AuthorDTI authorRequest) {

        try {
            Author existingAuthor = authorService.findById(id);

            if (existingAuthor == null) {
                ApiResponse<AuthorDTO> response = new ApiResponse<>("The author with the id provided doesn't exists", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            existingAuthor.setName(authorRequest.getName());
            existingAuthor.setBirthDate(authorRequest.getBirthDate());

            Author updatedAuthor = authorService.save(existingAuthor);

            if (updatedAuthor == null) {
                ApiResponse<AuthorDTO> response = new ApiResponse<>("Error when we try to updated the author, please try again", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();
            AuthorDTO authorDto = modelMapper.map(updatedAuthor, AuthorDTO.class);

            ApiResponse<AuthorDTO> response = new ApiResponse<>("Author updated successfully", authorDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<AuthorDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("author/{id}")
    public ResponseEntity<ApiResponse<AuthorDTO>> delete(@PathVariable Long id) {

        try {
            Author existingAuthor = authorService.findById(id);

            if (existingAuthor == null) {
                ApiResponse<AuthorDTO> response = new ApiResponse<>("The author with the id provided doesn't exists", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            authorService.deleteById(id);

            ModelMapper modelMapper = new ModelMapper();
            AuthorDTO authorDto = modelMapper.map(existingAuthor, AuthorDTO.class);

            ApiResponse<AuthorDTO> response = new ApiResponse<>("Author deleted successfully", authorDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<AuthorDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
