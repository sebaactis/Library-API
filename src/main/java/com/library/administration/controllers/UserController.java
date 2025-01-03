package com.library.administration.controllers;

import com.library.administration.models.dti.UserDTI;
import com.library.administration.models.dto.BookDTO;
import com.library.administration.models.dto.UserDTO;
import com.library.administration.models.entities.Book;
import com.library.administration.models.entities.User;
import com.library.administration.services.implementation.UserService;
import com.library.administration.utilities.ApiResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> findAll() {

        try {
            List<User> users = (List<User>) userService.findAll();

            if (users.isEmpty()) {
                ApiResponse<List<UserDTO>> response = new ApiResponse<>("We don't have any users", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();
            List<UserDTO> usersDTO = modelMapper.map(users, new TypeToken<List<UserDTO>>() {
            }.getType());

            ApiResponse<List<UserDTO>> response = new ApiResponse<>("Get users successfully", usersDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<List<UserDTO>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserDTO>> create(@Valid @RequestBody UserDTI userRequest) {
        try {
            User user = userService.createHashedUser(userRequest);

            if (user == null) {
                ApiResponse<UserDTO> response = new ApiResponse<>("We cant create a user, please try again", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);

            ApiResponse<UserDTO> response = new ApiResponse<>("User created successfully", userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> delete(@PathVariable Long userId) {
        try {
            User existingUser = userService.findById(userId);

            if (existingUser == null) {
                ApiResponse<UserDTO> response = new ApiResponse<>("The user with the id provided doesn't exists", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            userService.deleteById(userId);

            ModelMapper modelMapper = new ModelMapper();
            UserDTO userDto = modelMapper.map(existingUser, UserDTO.class);

            ApiResponse<UserDTO> response = new ApiResponse<>("User deleted successfully", userDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
