package com.library.administration.services.implementation;

import org.springframework.stereotype.Service;

import com.library.administration.models.dti.RoleDTI;
import com.library.administration.models.entities.Role;
import com.library.administration.repositories.RoleRepository;

@Service
public class RoleService extends ServiceImple<Role, Long> {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        super(roleRepository);
        this.roleRepository = roleRepository;
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public Role createRole(RoleDTI roleDTI) {

        System.out.println("Role received: " + roleRepository.findByName(roleDTI.getName()));

        if (roleRepository.findByName(roleDTI.getName()).isPresent()) {
            throw new IllegalArgumentException("Role already existS");
        }

        Role role = new Role();
        role.setName(roleDTI.getName());

        return save(role);
    }

    public Role editRole(Long id, RoleDTI roleDti) {

        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(roleDti.getName());

        return save(role);
    }

    public Role deleteRole(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));

        roleRepository.delete(role);

        return role;
    }

}
