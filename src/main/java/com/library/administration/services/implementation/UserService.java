package com.library.administration.services.implementation;

import com.library.administration.models.dti.UserDTI;
import com.library.administration.models.dti.UserEditDTI;
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

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("The email: " + userRequest.getEmail() + " is already in use, choose another");
        }

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

    public User editUser(User editUser, UserEditDTI requestUser) {

        if (requestUser.getUsername() != null) {
            editUser.setUsername(requestUser.getUsername());
        }
        if (requestUser.getEmail() != null) {
            editUser.setEmail(requestUser.getEmail());
        }
        if (requestUser.getRole() != null) {
            if (!isValidRole(requestUser.getRole())) {
                throw new IllegalArgumentException("Role not valid: " + requestUser.getRole());
            }

            editUser.setRole(Role.valueOf(requestUser.getRole().toUpperCase()));
        }

        return userRepository.save(editUser);
    }

    private boolean isValidRole(String role) {
        return Arrays.stream(Role.values()).anyMatch(enumValue -> enumValue.name().equalsIgnoreCase(role));
    }
}
