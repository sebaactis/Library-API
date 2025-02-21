package com.library.administration.controllers;

import com.library.administration.models.entities.Role;
import com.library.administration.services.UserRoleService;
import com.library.administration.utilities.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<String>> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            userRoleService.assignRoleToUser(userId, roleId);

            ApiResponse<String> response = new ApiResponse<>("Role assigned successfully", null);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<String>> removeRoleFromUser(@PathVariable Long userId,
            @PathVariable Long roleId) {
        try {
            userRoleService.removeRoleFromUser(userId, roleId);
            ApiResponse<String> response = new ApiResponse<>("Role removed successfully", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<List<Role>>> getUserRoles(@PathVariable Long userId) {
        try {
            List<Role> roles = userRoleService.getUserRoles(userId);
            ApiResponse<List<Role>> response = new ApiResponse<>("Roles retrieved successfully", roles);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ApiResponse<List<Role>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}