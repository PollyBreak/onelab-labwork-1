package com.polina.userservice.client;

import com.polina.dto.AuthRequest;
import com.polina.dto.AuthResponse;
import com.polina.dto.ChangePasswordRequest;
import com.polina.dto.TokenValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @PostMapping("/auth/login")
    String login(@RequestBody AuthRequest request );

    @PostMapping("/auth/register")
    AuthResponse register(@RequestBody AuthRequest request);

    @GetMapping("/auth/validate")
    TokenValidationResponse validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PutMapping("/auth/password")
    void changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody ChangePasswordRequest request);

    @DeleteMapping("/auth/{id}")
    void deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable Long id);

}
