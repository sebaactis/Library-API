package com.library.administration.services.implementation;

import com.library.administration.models.dti.UserDTI;
import com.library.administration.models.dti.UserEditDTI;
import com.library.administration.models.entities.Role;
import com.library.administration.models.entities.User;
import com.library.administration.repositories.RoleRepository;
import com.library.administration.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class UserService extends ServiceImple<User, Long> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User createHashedUser(UserDTI userRequest) {
    
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException(
                    "The email: " + userRequest.getEmail() + " is already in use, choose another");
        }
    
        List<Role> roles = userRequest.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName.toUpperCase())
                        .orElseThrow(() -> new IllegalArgumentException("Role not valid: " + roleName)))
                .collect(Collectors.toList());
    
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setRoles(roles);
    
        String passwordHashed = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(passwordHashed);
    
        return userRepository.save(user);
    }

    public User editUser(User editUser, UserEditDTI requestUser) {

        updateField(editUser::setUsername, requestUser.getUsername());
        updateField(editUser::setEmail, requestUser.getEmail());
        updateField(editUser::setProfilePictureUrl, requestUser.getProfilePictureUrl());
        updateField(editUser::setPreferences, requestUser.getPreferences());
    
        if (requestUser.getRoles() != null) {
            
            List<Role> roles = requestUser.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName.toUpperCase())
                            .orElseThrow(() -> new IllegalArgumentException("Role not valid: " + roleName)))
                    .collect(Collectors.toList());
            editUser.setRoles(roles);
        }
    
        return userRepository.save(editUser);
    }

    private <T> void updateField(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
