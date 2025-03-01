package com.library.administration.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.administration.models.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}