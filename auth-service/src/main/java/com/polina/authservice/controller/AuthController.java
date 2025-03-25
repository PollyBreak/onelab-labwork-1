package com.polina.authservice.controller;

import com.polina.authservice.dto.*;
import com.polina.authservice.service.AuthService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()
            || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse("Invalid input", "Invalid input", "ERROR"));
        }
        AuthResponse response = authService.registerUser(request.getUsername(), request.getPassword(), "USER");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Invalid input");
        }
        String token = authService.authenticateUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken
            (@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new TokenValidationResponse(false, null, null, null));
        }
        return ResponseEntity.ok(authService.validateToken(token));
    }


    @PutMapping("/password")
    @PreAuthorize("#request.username == authentication.principal or hasAuthority('ADMIN')")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request.getUsername(),
                request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal or hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                             @PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

}
