package com.library.administration.controllers;

import com.library.administration.models.dti.BookDTI;
import com.library.administration.models.dto.BookDTO;
import com.library.administration.models.entities.Book;
import com.library.administration.services.implementation.BookService;
import com.library.administration.services.implementation.RatingService;
import com.library.administration.utilities.ApiResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("books")
    public ResponseEntity<ApiResponse<Page<BookDTO>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        try {

            if (size <= 0 || page < 0) {
                ApiResponse<Page<BookDTO>> response = new ApiResponse<>("Size or page not valid (size must be > 0 and page 0 or more)", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Page<BookDTO> books = bookService.findAll(page, size, sort, direction);

            if (books.isEmpty()) {
                ApiResponse<Page<BookDTO>> response = new ApiResponse<>("We don´t have books yet", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ApiResponse<Page<BookDTO>> response = new ApiResponse<>("Get books successfully", books);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Page<BookDTO>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("book/{bookId}")
    public ResponseEntity<ApiResponse<BookDTO>> findById(@PathVariable Long bookId) {

        try {
            Book book = bookService.findById(bookId);

            if (book == null) {
                ApiResponse<BookDTO> response = new ApiResponse<>("Book not found, please try with another", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();
            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);

            ApiResponse<BookDTO> response = new ApiResponse<>("Book found!", bookDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<BookDTO> response = new ApiResponse<>(e.getMessage(), null);
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
    public ResponseEntity<ApiResponse<BookDTO>> changeAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        try {
            Book editBook = bookService.changeAuthor(bookId, authorId);

            if (editBook == null) {
                ApiResponse<BookDTO> response = new ApiResponse<>("We cant edit the book, please verify and try again", null);
                return ResponseEntity.status(400).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();
            BookDTO bookDTO = modelMapper.map(editBook, BookDTO.class);

            ApiResponse<BookDTO> response = new ApiResponse<>("Author from this book edited successfully", bookDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ApiResponse<BookDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<BookDTO>> edit(@PathVariable Long bookId, @Valid @RequestBody BookDTI requestBook) {
        try {
            Book editBook = bookService.findById(bookId);

            if (editBook == null) {
                ApiResponse<BookDTO> response = new ApiResponse<>("Book not found!", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (requestBook.getTitle() != null) {
                editBook.setTitle(requestBook.getTitle());
            }
            if (requestBook.getGenre() != null) {
                editBook.setGenre(requestBook.getGenre());
            }
            if (requestBook.getPublished() != null) {
                editBook.setPublished(requestBook.getPublished());
            }

            Book bookSave = bookService.save(editBook);

            ModelMapper modelMapper = new ModelMapper();
            BookDTO bookDTO = modelMapper.map(bookSave, BookDTO.class);

            ApiResponse<BookDTO> response = new ApiResponse<>("Book edited successfully", bookDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<BookDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<BookDTO>> delete(@PathVariable Long bookId) {
        try {
            Book existingBook = bookService.findById(bookId);

            if (existingBook == null) {
                ApiResponse<BookDTO> response = new ApiResponse<>("The book with the id provided doesn't exists", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            bookService.deleteById(bookId);

            ModelMapper modelMapper = new ModelMapper();
            BookDTO bookDto = modelMapper.map(existingBook, BookDTO.class);

            ApiResponse<BookDTO> response = new ApiResponse<>("Book deleted successfully", bookDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<BookDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
