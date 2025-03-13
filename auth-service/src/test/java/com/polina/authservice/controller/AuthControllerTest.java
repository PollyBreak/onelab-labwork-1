package com.polina.authservice.controller;

import com.polina.authservice.client.UserClient;
import com.polina.authservice.dto.UserDTO;
import com.polina.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @InjectMocks
    private AuthController authController;
    @Mock
    private UserClient userClient;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        authController = new AuthController(userClient, jwtUtil, passwordEncoder);
    }

    @Test
    void testRegister() {
        Map<String, String> request = Map.of("username", "testUser",
                "email", "test@example.com", "password", "password123");
        UserDTO userDTO = new UserDTO(null, "testUser", "test@example.com",
                "password123");
        when(userClient.registerUser(userDTO)).thenReturn("User registered successfully");

        String response = authController.register(request);
        assertEquals("User registered successfully", response);
    }

    @Test
    void testLogin_Success() {
        Map<String, String> request = Map.of("username", "testUser", "password",
                "password123");
        UserDTO user = new UserDTO(1L, "testUser", "test@example.com",
                "encodedPassword");
        when(userClient.getUserByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);
        when(jwtUtil.generateToken("testUser")).thenReturn("jwtToken");

        Map<String, String> response = authController.login(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.get("token"));
    }

    @Test
    void testLogin_InvalidCredentials() {
        Map<String, String> request = Map.of("username", "testUser",
                "password", "wrongPassword");
        UserDTO user = new UserDTO(1L, "testUser", "test@example.com",
                "encodedPassword");
        when(userClient.getUserByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> authController.login(request));
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testLogin_UserNotFound() {
        Map<String, String> request = Map.of("username", "unknownUser",
                "password", "password123");
        when(userClient.getUserByUsername("unknownUser")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> authController.login(request));
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testValidateToken_Success() {
        String token = "Bearer validToken";
        when(jwtUtil.extractUsername("validToken")).thenReturn("testUser");
        when(userClient.getUserIdByUsername("testUser")).thenReturn(1L);

        ResponseEntity<Long> response = authController.validateToken(token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody());
    }

    @Test
    void testValidateToken_Invalid() {
        String token = "Bearer invalidToken";
        when(jwtUtil.extractUsername("invalidToken"))
                .thenThrow(new RuntimeException("Invalid token"));
        ResponseEntity<Long> response = authController.validateToken(token);
        assertEquals(401, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
