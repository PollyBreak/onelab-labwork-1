package com.polina.userservice.controller;

import com.polina.userservice.dto.UserDTO;
import com.polina.userservice.entity.User;
import com.polina.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

//    @PostMapping
//    public ResponseEntity<String> createUser(@RequestBody UserDTO user) {
//        userService.saveUser(user);
//        return ResponseEntity.ok("User created successfully");
//    }
    @PostMapping("/register")
    public String registerUser(@RequestBody UserDTO userDTO) {
        return userService.registerUser(userDTO);
    }

    @GetMapping("/by-username/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/id/{username}")
    public ResponseEntity<Long> getUserIdByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserIdByUsername(username));
    }

}
