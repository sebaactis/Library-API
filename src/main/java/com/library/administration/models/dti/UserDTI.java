package com.library.administration.models.dti;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTI {
    @NotNull(message = "Username cannot be empty")
    @Size(min = 6, max = 15, message = "The username need to have at least 6 characters and not more than 15 characters")
    private String username;

    @NotNull(message = "Password cannot be empty")
    @Size(min = 8, max = 15, message = "The password need to have at least 8 characters and not more than 15 characters")
    private String password;

    @Email
    @NotNull(message = "Email cannot be empty")
    private String email;

    @NotNull(message = "Role cannot be empty")
    private String role;

    @Size(max = 500, message = "The size of the picture cannot exceed 500 characters")
    private String profilePictureUrl;

    @Size(max = 200, message = "Preferences cannot exceed 200 characters")
    private String preferences;
}
