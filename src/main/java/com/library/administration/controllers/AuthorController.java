package com.library.administration.controllers;

import com.library.administration.models.dto.AuthorDTO;
import com.library.administration.models.entities.Author;
import com.library.administration.services.implementation.AuthorService;
import com.library.administration.utilities.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping("authors")
    public ResponseEntity<ApiResponse<List<Author>>> findAll() {
        List<Author> authors = (List<Author>) authorService.findAll();

        if (authors.isEmpty()) {
            ApiResponse<List<Author>> response = new ApiResponse<>("We don´t have authors yet", null);
            return ResponseEntity.status(404).body(response);
        }

        ApiResponse<List<Author>> response = new ApiResponse<>("Get authors successfully", authors);
        return ResponseEntity.ok(response);
    }

    @GetMapping("author/{id}")
    public ResponseEntity<ApiResponse<Author>> findById(@PathVariable Long id) {
        Author author = authorService.findById(id);

        if (author == null) {
            ApiResponse<Author> response = new ApiResponse<>("Author not found, please try with another", null);
            return ResponseEntity.status(404).body(response);
        }

        ApiResponse<Author> response = new ApiResponse<>("Author found!", author);
        return ResponseEntity.ok(response);
    }

    @PostMapping("author")
    public ResponseEntity<ApiResponse<Author>> create(@RequestBody AuthorDTO authorRequest) {
        Author author = new Author();
        author.setName(authorRequest.getName());
        author.setBirthDate(authorRequest.getBirthDate());

        Author savedAuthor = authorService.save(author);

        if (savedAuthor == null) {
            ApiResponse<Author> response = new ApiResponse<>("Author created failed, try again", null);
            return ResponseEntity.status(400).body(response);
        }

        ApiResponse<Author> response = new ApiResponse<>("Author created successfully", author);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("author/{id}")
    public ResponseEntity<ApiResponse<Author>> update(@PathVariable Long id, @RequestBody AuthorDTO authorRequest) {
        Author existingAuthor = authorService.findById(id);

        if(existingAuthor == null) {
            ApiResponse<Author> response = new ApiResponse<>("The author with the id provided doesn't exists", null);
            return ResponseEntity.status(404).body(response);
        }

        existingAuthor.setName(authorRequest.getName());
        existingAuthor.setBirthDate(authorRequest.getBirthDate());

        Author updatedAuthor = authorService.save(existingAuthor);

        if(updatedAuthor == null) {
            ApiResponse<Author> response = new ApiResponse<>("Error when we try to updated the author, please try again", null);
            return ResponseEntity.status(400).body(response);
        }

        ApiResponse<Author> response = new ApiResponse<>("Author updated successfully", updatedAuthor);
        return ResponseEntity.status(200).body(response);

    }

    @DeleteMapping("author/{id}")
    public ResponseEntity<ApiResponse<Author>> delete(@PathVariable Long id) {
        Author existingAuthor = authorService.findById(id);

        if(existingAuthor == null) {
            ApiResponse<Author> response = new ApiResponse<>("The author with the id provided doesn't exists", null);
            return ResponseEntity.status(404).body(response);
        }

        authorService.deleteById(id);
        ApiResponse<Author> response = new ApiResponse<>("Author deleted successfully", existingAuthor);
        return ResponseEntity.status(200).body(response);
    }
}
