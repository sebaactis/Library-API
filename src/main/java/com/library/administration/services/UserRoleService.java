package com.library.administration.services;

import com.library.administration.models.entities.Role;
import com.library.administration.models.entities.User;
import com.library.administration.repositories.RoleRepository;
import com.library.administration.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        if (user.getRoles().contains(role)) {
            throw new IllegalArgumentException("The user just has this role");
        }

        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Transactional
    public User removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        if (!user.getRoles().contains(role)) {
            throw new IllegalArgumentException("The user does not have this role");
        }

        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    public List<Role> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getRoles();
    }
}