package com.library.administration.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishListDTO {
    private Long id;
    private UserDTO user;
    private BookDTO book;
    private LocalDateTime addedAt;
}
