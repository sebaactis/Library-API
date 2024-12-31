package com.library.administration.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String genre;
    private LocalDate published;
}
