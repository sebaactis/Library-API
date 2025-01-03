package com.library.administration.services.implementation;

import com.library.administration.models.dti.UserDTI;
import com.library.administration.models.entities.Role;
import com.library.administration.models.entities.User;
import com.library.administration.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserService extends ServiceImple<User, Long> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createHashedUser(UserDTI userRequest) {

        if (!isValidRole(userRequest.getRole())) {
            throw new IllegalArgumentException("Role not valid: " + userRequest.getRole());
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));

        String passwordHashed = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(passwordHashed);

        return userRepository.save(user);
    }

    private boolean isValidRole(String role) {
        return Arrays.stream(Role.values()).anyMatch(enumValue -> enumValue.name().equalsIgnoreCase(role));
    }
}
