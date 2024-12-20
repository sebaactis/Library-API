package com.library.administration.controllers;

import com.library.administration.models.entities.Book;
import com.library.administration.services.implementation.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("books")
    public List<Book> findAll() {
        return (List<Book>) bookService.findAll();
    }
}
