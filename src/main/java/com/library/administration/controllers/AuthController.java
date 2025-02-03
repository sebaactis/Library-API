package com.library.administration.controllers;

import com.library.administration.models.dti.LoginDTI;
import com.library.administration.models.dti.LoginDTO;
import com.library.administration.models.dti.RegisterDTI;
import com.library.administration.models.dti.RegisterDTO;
import com.library.administration.models.entities.Author;
import com.library.administration.models.entities.User;
import com.library.administration.services.AuthService;
import com.library.administration.utilities.ApiResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterDTO>> register(@Valid @RequestBody RegisterDTI userRegister) {
        try {
            User user = authService.register(userRegister);

            if (user == null) {
                throw new IllegalStateException("Failed to register user");
            }

            RegisterDTO userRegisterDTO = new ModelMapper().map(user, RegisterDTO.class);
            ApiResponse<RegisterDTO> response = new ApiResponse<>("User registered successfully", userRegisterDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse<RegisterDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginDTO>> login(@Valid @RequestBody LoginDTI userLogin) {
        try {
            String token = authService.login(userLogin);

            if (token == null) {
                throw new IllegalStateException("Failed to login");
            }

            LoginDTO userLoginDTO = new LoginDTO(
                    userLogin.getEmail(),
                    userLogin.getUsername(),
                    token
            );

            ApiResponse<LoginDTO> response = new ApiResponse<>("Login successfully", userLoginDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<LoginDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
