package com.library.administration.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.administration.models.dti.RoleDTI;
import com.library.administration.models.entities.Role;
import com.library.administration.services.implementation.RoleService;
import com.library.administration.utilities.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping()
    public ResponseEntity<ApiResponse<Iterable<Role>>> findAll() {

        try {
            Iterable<Role> roles = roleService.findAll();

            if (roles == null) {
                ApiResponse<Iterable<Role>> response = new ApiResponse<Iterable<Role>>("We dont have roles yet", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ApiResponse<Iterable<Role>> response = new ApiResponse<Iterable<Role>>("Roles found", roles);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<Iterable<Role>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<Role>> findByName(@PathVariable String name) {
        try {
            Role role = roleService.findByName(name);

            if (role == null) {
                ApiResponse<Role> response = new ApiResponse<>("Role not found", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ApiResponse<Role> response = new ApiResponse<>("Role found", role);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<Role> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Role>> create(@Valid @RequestBody RoleDTI role) {
        try {
            Role roleCreated = roleService.createRole(role);

            if (roleCreated == null) {
                ApiResponse<Role> response = new ApiResponse<>("Role not created", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            ApiResponse<Role> response = new ApiResponse<>("Role created", roleCreated);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<Role> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponse<Role>> edit(@PathVariable Long id, @Valid @RequestBody RoleDTI role) {
        try {
            Role roleEdited = roleService.editRole(id, role);

            if (roleEdited == null) {
                ApiResponse<Role> response = new ApiResponse<>("Role not edited", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ApiResponse<Role> response = new ApiResponse<>("Role edited", roleEdited);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<Role> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        try {
            Role roleDeleted = roleService.deleteRole(id);

            if (roleDeleted == null) {
                ApiResponse<String> response = new ApiResponse<>("Role not deleted", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ApiResponse<String> response = new ApiResponse<>("Role deleted", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
