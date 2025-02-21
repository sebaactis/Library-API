package com.library.administration.models.dti;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTI {

    @NotBlank
    @Size(min = 3, max = 10, message = "The name of the rol must be between 3 and 10 characters")
    private String name;
}
