package com.library.administration.controllers;

import com.library.administration.models.dti.LoginDTI;
import com.library.administration.models.dti.LoginDTO;
import com.library.administration.models.dti.RegisterDTI;
import com.library.administration.models.dti.RegisterDTO;
import com.library.administration.models.entities.Author;
import com.library.administration.models.entities.User;
import com.library.administration.services.AuthService;
import com.library.administration.utilities.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
            Map<String, String> tokens = authService.login(userLogin);

            if (tokens == null) {
                throw new IllegalStateException("Failed to login");
            }

            LoginDTO userLoginDTO = new LoginDTO(
                    userLogin.getEmail(),
                    userLogin.getUsername(),
                    tokens.get("accessToken"),
                    tokens.get("refreshToken")
            );

            ApiResponse<LoginDTO> response = new ApiResponse<>("Login successfully", userLoginDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<LoginDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestBody String token) {
        try {
            String newAccessToken = authService.refreshToken(token);
            ApiResponse<String> response = new ApiResponse<>("Token has been refreshed successfully", newAccessToken);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                authService.revokeToken(token);

                ApiResponse<String> response = new ApiResponse<>("Logout success", "");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            ApiResponse<String> response = new ApiResponse<>("Logout failed", "");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
