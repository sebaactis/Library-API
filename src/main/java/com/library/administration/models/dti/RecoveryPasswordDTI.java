package com.library.administration.models.dti;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecoveryPasswordDTI {
     
    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String token;

}
