package com.library.administration.security;

import com.library.administration.models.entities.Token;
import com.library.administration.repositories.TokenRepository;
import com.library.administration.services.TokenRefreshService;
import com.library.administration.utilities.cookies.CookieUtil;
import com.library.administration.utilities.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final TokenRefreshService tokenRefreshService;
    private final CookieUtil cookieUtil;

    public JwtAuthFilter(JwtUtil jwtUtil, TokenRepository tokenRepository, TokenRefreshService tokenRefreshService, CookieUtil cookieUtil) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.tokenRefreshService = tokenRefreshService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        try {
            if (accessToken == null) {
                throw new IllegalArgumentException("Access token not found");
            }

            Token tokenEntity = tokenRepository.findByToken(accessToken)
                    .orElseThrow(() -> new IllegalArgumentException("Token not found"));

            if (jwtUtil.IsTokenExpired(accessToken)) {
                if (refreshToken == null) {
                    throw new IllegalArgumentException("Refresh token not found");
                }

                String newAccessToken = tokenRefreshService.refreshToken(refreshToken);
                Cookie cookie = cookieUtil.createCookie("accessToken", newAccessToken, false);
                response.addCookie(cookie);
                tokenEntity.setExpired(true);
                accessToken = newAccessToken;
            }

            if (tokenEntity.isRevoked() || tokenEntity.isExpired()) {
                throw new IllegalArgumentException("Token revoked or expired");
            }

            String email = jwtUtil.extractEmail(accessToken);
            Claims claims = jwtUtil.extractClaims(accessToken);
            String role = claims.get("role", String.class);

            if (email != null && role != null) {
                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                UserDetails userDetails = new User(email, "", authorities);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("Usuario autenticado: " + email + ", Roles: " + role);

            }
        } catch (Exception e) {
            System.out.println("JWT invalido: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }
}
