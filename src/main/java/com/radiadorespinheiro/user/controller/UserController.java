package com.radiadorespinheiro.user.controller;

import com.radiadorespinheiro.user.dto.CreateUserRequest;
import com.radiadorespinheiro.user.dto.UserResponse;
import com.radiadorespinheiro.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create user", description = "Creates a new user with encrypted password")
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @Operation(summary = "List all users")
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Find user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Update user")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @Operation(summary = "Toggle user active status")
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<UserResponse> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleActive(id));
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}