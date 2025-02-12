package com.library.administration.models.dti;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEditDTI {
    @Size(min = 6, max = 15, message = "The username need to have at least 6 characters and not more than 15 characters")
    private String username;

    @Size(min = 8, max = 15, message = "The password need to have at least 8 characters and not more than 15 characters")
    private String password;

    @Email
    private String email;

    private String role;

    private String profilePictureUrl;

    private String preferences;
}
