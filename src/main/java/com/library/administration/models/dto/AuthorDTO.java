package com.library.administration.models.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class AuthorDTO {

    private String name;
    private LocalDate birthDate;
    private List<BookDTO> books;
}
