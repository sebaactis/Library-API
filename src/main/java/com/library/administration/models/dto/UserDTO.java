package com.library.administration.models.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String profilePictureUrl;
    private String preferences;
}
