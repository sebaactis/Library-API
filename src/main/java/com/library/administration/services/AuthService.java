package com.library.administration.services;

import com.library.administration.models.dti.LoginDTI;
import com.library.administration.models.dti.RegisterDTI;
import com.library.administration.models.entities.Role;
import com.library.administration.models.entities.Token;
import com.library.administration.models.entities.User;
import com.library.administration.repositories.TokenRepository;
import com.library.administration.repositories.UserRepository;
import com.library.administration.utilities.Jwt.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
    }

    public User register(RegisterDTI userRegister) {
        if (userRepository.existsByEmail(userRegister.getEmail())) {
            throw new IllegalArgumentException("User already registered");
        }

        if (!userRegister.getPassword().equalsIgnoreCase(userRegister.getConfirmPassword())) {
            throw new IllegalArgumentException("The passwords do not match");
        }

        User user = new User();
        user.setEmail(userRegister.getEmail());
        user.setUsername(userRegister.getUsername());
        user.setPassword(passwordEncoder.encode(userRegister.getPassword()));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    public String login(LoginDTI userLogin) {
        Optional<User> userFind = userRepository.findByEmail(userLogin.getEmail());

        if (userFind.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        if (!passwordEncoder.matches(userLogin.getPassword(), userFind.get().getPassword())) {
            throw new IllegalArgumentException("Invalid Password");
        }

        return generateToken(userFind.get());
    }

    public String generateToken(User user) {
        String token = jwtUtil.generateToken(user.getEmail());
        saveToken(user.getEmail(), token);
        return token;
    }

    private void saveToken(String email, String token) {
        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setEmail(email);
        tokenEntity.setExpirationDate(jwtUtil.extractClaims(token).getExpiration());
        tokenEntity.setRevoked(false);
        tokenEntity.setExpired(false);
        tokenRepository.save(tokenEntity);
    }

    public String refreshToken(String refreshToken) {
        if (jwtUtil.IsTokenExpired(refreshToken)) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        String newToken = jwtUtil.generateToken(email);
        revokeToken(refreshToken);
        saveToken(email, newToken);
        return newToken;

    }

    private void revokeToken(String token) {
        Token tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));
        tokenEntity.setRevoked(true);
        tokenRepository.save(tokenEntity);
    }
}
