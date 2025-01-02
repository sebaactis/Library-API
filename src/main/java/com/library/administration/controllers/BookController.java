package com.library.administration.controllers;

import com.library.administration.models.dti.BookDTI;
import com.library.administration.models.entities.Book;
import com.library.administration.services.implementation.BookService;
import com.library.administration.utilities.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("books")
    public ResponseEntity<ApiResponse<List<Book>>> findAll() {
        List<Book> authors = (List<Book>) bookService.findAll();

        if (authors.isEmpty()) {
            ApiResponse<List<Book>> response = new ApiResponse<>("We don´t have books yet", null);
            return ResponseEntity.status(404).body(response);
        }

        ApiResponse<List<Book>> response = new ApiResponse<>("Get authors successfully", authors);
        return ResponseEntity.ok(response);
    }

    @PostMapping("book/{authorId}")
    public ResponseEntity<ApiResponse<Book>> create(@RequestBody BookDTI bookRequest, @PathVariable Long authorId) {

        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setGenre(bookRequest.getGenre());
        book.setPublished(bookRequest.getPublished());

        Book createBook = bookService.createBookWithAuthor(book, authorId);

        if (createBook == null) {
            ApiResponse<Book> response = new ApiResponse<>("We cant create the book, please try again", null);
            return ResponseEntity.status(400).body(response);
        }

        ApiResponse<Book> response = new ApiResponse<>("Book created successfully", createBook);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/book/{bookId}/{authorId}")
    public ResponseEntity<ApiResponse<Book>> changeAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        Book editBook = bookService.changeAuthor(bookId, authorId);

        if (editBook == null) {
            ApiResponse<Book> response = new ApiResponse<>("We cant edit the book, pleasy verify and try again", null);
            return ResponseEntity.status(400).body(response);
        }

        ApiResponse<Book> response = new ApiResponse<>("Author from this book edited successfully", editBook);
        return ResponseEntity.status(200).body(response);
    }
}
