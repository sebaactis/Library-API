package com.library.administration.services;

import com.library.administration.models.dti.LoginDTI;
import com.library.administration.models.dti.RegisterDTI;
import com.library.administration.models.entities.Role;
import com.library.administration.models.entities.User;
import com.library.administration.repositories.UserRepository;
import com.library.administration.utilities.JwtConfig;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtConfig jwtConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfig = jwtConfig;
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
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey().getBytes())
                .compact();
    }
}
