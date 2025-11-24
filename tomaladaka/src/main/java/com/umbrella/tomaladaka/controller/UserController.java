package com.umbrella.tomaladaka.controller;

import com.umbrella.tomaladaka.dto.UserRequest;
import com.umbrella.tomaladaka.model.User;
import com.umbrella.tomaladaka.service.UserService; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "Manage users (clients, delivery personnel, etc.)")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    public ResponseEntity<User> createUser(@RequestBody UserRequest user) {
        User savedUser = userService.createUser(user);
        URI location = URI.create(String.format("/users/%d", savedUser.getId()));
        return ResponseEntity.created(location).body(savedUser);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users")
    @ApiResponse(responseCode = "200", description = "List of users retrieved")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user details")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by ID")
    @ApiResponse(responseCode = "204", description = "User deleted")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}