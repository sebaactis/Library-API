package com.library.administration.services;

import com.library.administration.models.entities.Role;
import com.library.administration.models.entities.Token;
import com.library.administration.repositories.TokenRepository;
import com.library.administration.utilities.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class TokenRefreshService {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public TokenRefreshService(JwtUtil jwtUtil, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
    }

    public String refreshToken(String refreshToken) {
        if (jwtUtil.IsTokenExpired(refreshToken)) {
            throw new IllegalArgumentException("Refresh token expired");
        }
        String email = jwtUtil.extractEmail(refreshToken);
        Claims claims = jwtUtil.extractClaims(refreshToken);
        String role = claims.get("role", String.class);

        Token tokenEntity = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));
        if (tokenEntity.isRevoked()) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        String newToken = jwtUtil.generateToken(email, Role.valueOf(role), false);
        tokenEntity.setRevoked(true);
        tokenRepository.save(tokenEntity);

        return newToken;
    }
}