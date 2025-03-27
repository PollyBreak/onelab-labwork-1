package com.polina.authservice.controller;

import com.polina.authservice.service.AuthService;
import com.polina.dto.AuthRequest;
import com.polina.dto.AuthResponse;
import com.polina.dto.ChangePasswordRequest;
import com.polina.dto.TokenValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_Success() {
        AuthRequest request = new AuthRequest("testUser", "password");
        AuthResponse mockResponse = new AuthResponse("testUser", "password", "USER");
        when(authService.registerUser(request.getUsername(), request.getPassword(), "USER"))
                .thenReturn(mockResponse);
        ResponseEntity<AuthResponse> response = authController.register(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser", response.getBody().getUsername());
    }

    @Test
    void register_Failure_InvalidInput() {
        AuthRequest request = new AuthRequest("", "password");
        ResponseEntity<AuthResponse> response = authController.register(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", response.getBody().getUsername());
    }

    @Test
    void login_Success() {
        AuthRequest request = new AuthRequest("testUser", "password");
        when(authService.authenticateUser("testUser", "password"))
                .thenReturn("mockToken");
        ResponseEntity<String> response = authController.login(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mockToken", response.getBody());
    }

    @Test
    void login_Failure_InvalidInput() {
        AuthRequest request = new AuthRequest("", "password");
        ResponseEntity<String> response = authController.login(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", response.getBody());
    }

    @Test
    void validateToken_Success() {
        String token = "Bearer validToken";
        TokenValidationResponse mockResponse = new TokenValidationResponse
                (true, 1L, "testUser", "USER");
        when(authService.validateToken(token)).thenReturn(mockResponse);
        ResponseEntity<TokenValidationResponse> response = authController.validateToken(token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isValid());
    }

    @Test
    void validateToken_Failure_EmptyToken() {
        ResponseEntity<TokenValidationResponse> response = authController.validateToken("");
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().isValid());
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest
                ("testUser", "oldPass", "newPass");
        ResponseEntity<String> response = authController.changePassword(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());
        verify(authService, times(1))
                .changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
    }

    @Test
    void deleteUser_Success() {
        ResponseEntity<String> response = authController
                .deleteUser("Bearer validToken", 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
        verify(authService, times(1)).deleteUser(1L);
    }
}
