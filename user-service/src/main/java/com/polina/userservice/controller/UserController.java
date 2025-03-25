package com.polina.userservice.controller;

import com.polina.userservice.service.UserService;
import com.polina.dto.AuthRequest;
import com.polina.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestBody AuthRequest authRequest) {
        return userService.registerUser(authRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal or hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        userService.deleteUser(id, token);
        return ResponseEntity.ok("User deleted successfully");
    }


}
