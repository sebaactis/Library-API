package com.library.administration.services;

import com.library.administration.models.entities.Role;
import com.library.administration.models.entities.Token;
import com.library.administration.models.entities.User;
import com.library.administration.repositories.RoleRepository;
import com.library.administration.repositories.TokenRepository;
import com.library.administration.repositories.UserRepository;
import com.library.administration.utilities.jwt.JwtUtil;
import io.jsonwebtoken.Claims;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TokenRefreshService {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public TokenRefreshService(JwtUtil jwtUtil, TokenRepository tokenRepository, RoleRepository roleRepository,
            UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public String refreshToken(String refreshToken) {
        if (jwtUtil.IsTokenExpired(refreshToken)) {
            throw new IllegalArgumentException("Refresh token expired");
        }
        String email = jwtUtil.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Role> roles = user.getRoles();

        Token tokenEntity = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));
        if (tokenEntity.isRevoked()) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        String newToken = jwtUtil.generateToken(email, roles, false);
        tokenEntity.setRevoked(true);
        tokenRepository.save(tokenEntity);

        return newToken;
    }
}