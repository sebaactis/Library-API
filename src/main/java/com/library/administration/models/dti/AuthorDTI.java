package com.library.administration.models.dti;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AuthorDTI {

    @NotNull(message = "Name cannot be empty")
    @Size(min = 3, max = 40, message = "The name must be between 3 and 50 characters")
    private String name;

    @NotNull(message = "BirthDate cannot be empty")
    private LocalDate birthDate;
}
