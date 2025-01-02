package com.library.administration.controllers;

import com.library.administration.models.dti.BookDTI;
import com.library.administration.models.dto.AuthorDTO;
import com.library.administration.models.dto.BookDTO;
import com.library.administration.models.entities.Book;
import com.library.administration.services.implementation.BookService;
import com.library.administration.utilities.ApiResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("books")
    public ResponseEntity<ApiResponse<List<BookDTO>>> findAll() {
        try {
            List<Book> books = (List<Book>) bookService.findAll();

            if (books.isEmpty()) {
                ApiResponse<List<BookDTO>> response = new ApiResponse<>("We don´t have books yet", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();
            List<BookDTO> booksDTO = modelMapper.map(books, new TypeToken<List<BookDTO>>() {
            }.getType());

            ApiResponse<List<BookDTO>> response = new ApiResponse<>("Get authors successfully", booksDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<List<BookDTO>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PostMapping("book/{authorId}")
    public ResponseEntity<ApiResponse<BookDTO>> create(@Valid @RequestBody BookDTI bookRequest, @PathVariable Long authorId) {

        try {
            Book book = new Book();
            book.setTitle(bookRequest.getTitle());
            book.setGenre(bookRequest.getGenre());
            book.setPublished(bookRequest.getPublished());

            Book createBook = bookService.createBookWithAuthor(book, authorId);

            if (createBook == null) {
                ApiResponse<BookDTO> response = new ApiResponse<>("We cant create the book, please try again", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();
            BookDTO bookDTO = modelMapper.map(createBook, BookDTO.class);

            ApiResponse<BookDTO> response = new ApiResponse<>("Book created successfully", bookDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            ApiResponse<BookDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
