package com.library.administration.controllers;

import com.library.administration.models.dti.LoginDTI;
import com.library.administration.models.dti.RecoveryPasswordDTI;
import com.library.administration.models.dti.RegisterDTI;
import com.library.administration.models.dto.RegisterDTO;
import com.library.administration.models.dto.LoginDTO;
import com.library.administration.models.entities.User;
import com.library.administration.services.AuthService;
import com.library.administration.services.TokenRefreshService;
import com.library.administration.utilities.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Autowired
    private TokenRefreshService tokenRefreshService;

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
    public ResponseEntity<ApiResponse<LoginDTO>> login(@Valid @RequestBody LoginDTI userLogin,
            HttpServletResponse httpResponse) {
        try {
            authService.login(httpResponse, userLogin);

            LoginDTO userLoginDTO = new LoginDTO(
                    userLogin.getEmail());

            ApiResponse<LoginDTO> response = new ApiResponse<>("Login successfully", userLoginDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<LoginDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/reset-password-init")
    public ResponseEntity<ApiResponse<String>> recoveryPassword(@RequestBody String email) {
        try {
            String recoveryToken = authService.resetPasswordInit("sebaactis@gmail.com");

            if (recoveryToken == null) {
                ApiResponse<String> response = new ApiResponse<>("Fail when try recovery password", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ApiResponse<String> response = new ApiResponse<>("Reset password request sended successfully",
                    recoveryToken);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody @Valid RecoveryPasswordDTI recoveryPasswordDTI) {
        try {
            String recovery = authService.resetPassword(recoveryPasswordDTI);

            if(recovery == null) {
                ApiResponse<String> response = new ApiResponse<>("Password cannot be reset, try again", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ApiResponse<String> response = new ApiResponse<>("Password has been reset successfully", recovery);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestBody String token) {
        try {
            String newAccessToken = tokenRefreshService.refreshToken(token);
            ApiResponse<String> response = new ApiResponse<>("Token has been refreshed successfully", newAccessToken);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @SuppressWarnings("null")
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
