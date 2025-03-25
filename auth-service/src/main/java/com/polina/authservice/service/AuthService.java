package com.polina.authservice.service;

import com.polina.authservice.dto.AuthResponse;
import com.polina.authservice.dto.TokenValidationResponse;
import com.polina.authservice.entity.Role;
import com.polina.authservice.entity.UserAuth;
import com.polina.authservice.repository.UserAuthRepository;
import com.polina.authservice.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse registerUser(String username, String password, String role) {
        if (userAuthRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }
        UserAuth user = new UserAuth();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Enum.valueOf(Role.class, role));
        userAuthRepository.save(user);
        return new AuthResponse(username, password, role);
    }

    public String authenticateUser(String username, String password) {
        UserAuth user = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
    }


    public TokenValidationResponse validateToken(String token) {
        return jwtUtil.validateToken(token);
    }


    public void changePassword(String username, String oldPassword, String newPassword) {
        UserAuth user = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userAuthRepository.save(user);
    }


    public void deleteUser(Long id) {
        UserAuth user = userAuthRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userAuthRepository.delete(user);
    }

}
