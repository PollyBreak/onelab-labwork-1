package com.polina.authservice.controller;

import com.polina.authservice.client.UserClient;
import com.polina.authservice.dto.UserDTO;
import com.polina.authservice.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserClient userClient;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserClient userClient, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userClient = userClient;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        UserDTO userDTO = new UserDTO(null, username, email, password);
        return userClient.registerUser(userDTO);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        UserDTO user = userClient.getUserByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(username);

        return Map.of("token", token);
    }

//    @GetMapping("/validate")
//    public ResponseEntity<?> validateToken(@RequestParam String token) {
//        boolean isValid = jwtUtil.validateToken(token);
//        if (isValid) {
//            String username = jwtUtil.extractUsername(token);
//            return ResponseEntity.ok("Token is valid for user: " + username);
//        } else {
//            return ResponseEntity.status(401).body("Invalid or expired token.");
//        }
//    }

    @GetMapping("/validate")
    public ResponseEntity<Long> validateToken(@RequestHeader("Authorization") String token) {
        try {
            token = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);
            Long userId = userClient.getUserIdByUsername(username);
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
