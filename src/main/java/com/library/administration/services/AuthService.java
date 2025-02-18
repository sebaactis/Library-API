package com.library.administration.services;

import com.library.administration.models.dti.LoginDTI;
import com.library.administration.models.dti.RecoveryPasswordDTI;
import com.library.administration.models.dti.RegisterDTI;
import com.library.administration.models.entities.Role;
import com.library.administration.models.entities.Token;
import com.library.administration.models.entities.User;
import com.library.administration.repositories.TokenRepository;
import com.library.administration.repositories.UserRepository;
import com.library.administration.utilities.cookies.CookieUtil;
import com.library.administration.utilities.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final CookieUtil cookieUtil;
    private final MailService mailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            TokenRepository tokenRepository, CookieUtil cookieUtil, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.cookieUtil = cookieUtil;
        this.mailService = mailService;
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

    public void login(HttpServletResponse response, LoginDTI userLogin) {
        Optional<User> userFind = userRepository.findByEmail(userLogin.getEmail());

        if (userFind.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        if (!passwordEncoder.matches(userLogin.getPassword(), userFind.get().getPassword())) {
            throw new IllegalArgumentException("Invalid Password");
        }

        String accessToken = generateToken(userFind.get(), false);
        String refreshToken = generateToken(userFind.get(), true);

        saveToken(userFind.get().getEmail(), accessToken);
        saveToken(userFind.get().getEmail(), refreshToken);

        Cookie accessTokenCookie = cookieUtil.createCookie("accessToken", accessToken, false);
        Cookie refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken, true);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    public String resetPasswordInit(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String recoveryToken = generateToken(user, false);
        saveToken(email, recoveryToken);

        mailService.sendPasswordResetEmail(email, recoveryToken);

        return email;
    }

    public String resetPassword(RecoveryPasswordDTI recoveryPasswordDTI, String token) {

        Token tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (tokenEntity.isRevoked() || tokenEntity.isExpired()) {
            throw new RuntimeException("Token is invalid or expired");
        }

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!recoveryPasswordDTI.getPassword().equalsIgnoreCase(recoveryPasswordDTI.getConfirmPassword())) {
            throw new IllegalArgumentException("The passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(recoveryPasswordDTI.getPassword()));
        userRepository.save(user);

        return email;

    }

    public String generateToken(User user, boolean isRefresh) {
        return jwtUtil.generateToken(user.getEmail(), user.getRole(), isRefresh);
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

    public void revokeToken(String token) {
        Token tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));
        tokenEntity.setRevoked(true);
        tokenRepository.save(tokenEntity);
    }
}
