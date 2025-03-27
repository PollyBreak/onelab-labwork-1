package com.polina.authservice.service;

import com.polina.authservice.entity.Role;
import com.polina.authservice.entity.UserAuth;
import com.polina.authservice.repository.UserAuthRepository;
import com.polina.authservice.security.JwtUtil;
import com.polina.dto.AuthResponse;
import com.polina.dto.TokenValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        String username = "testUser";
        String password = "password";
        String role = "USER";

        when(userAuthRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        AuthResponse response = authService.registerUser(username, password, role);

        assertNotNull(response);
        assertEquals(username, response.getUsername());
        assertEquals(password, response.getPassword());
        assertEquals(role, response.getRole());
        verify(userAuthRepository, times(1)).save(any(UserAuth.class));
    }

    @Test
    void registerUser_Failure_UserAlreadyExists() {
        String username = "testUser";
        when(userAuthRepository.findByUsername(username)).thenReturn(Optional.of(new UserAuth()));
        assertThrows(IllegalArgumentException.class, () -> authService
                .registerUser(username, "password", "USER"));
    }

    @Test
    void authenticateUser_Success() {
        String username = "testUser";
        String password = "password";
        UserAuth user = new UserAuth();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        when(userAuthRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(1L, username, "USER")).thenReturn("mockToken");
        String token = authService.authenticateUser(username, password);

        assertEquals("mockToken", token);
    }

    @Test
    void authenticateUser_Failure_UserNotFound() {
        when(userAuthRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService
                .authenticateUser("testUser", "password"));
    }

    @Test
    void authenticateUser_Failure_InvalidCredentials() {
        String username = "testUser";
        String password = "password";
        UserAuth user = new UserAuth();
        user.setPassword("encodedPassword");
        when(userAuthRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authService
                .authenticateUser(username, password));
    }

    @Test
    void validateToken() {
        TokenValidationResponse mockResponse = new TokenValidationResponse
                (true, 1L, "testUser", "USER");
        when(jwtUtil.validateToken("Bearer validToken")).thenReturn(mockResponse);
        TokenValidationResponse response = authService.validateToken("Bearer validToken");
        assertNotNull(response);
        assertTrue(response.isValid());
        assertEquals("testUser", response.getUsername());
        assertEquals("USER", response.getRole());
    }

    @Test
    void changePassword_Success() {
        String username = "testUser";
        String oldPassword = "oldPass";
        String newPassword = "newPass";
        UserAuth user = new UserAuth();
        user.setUsername(username);
        user.setPassword("encodedOldPassword");

        when(userAuthRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        authService.changePassword(username, oldPassword, newPassword);
        verify(userAuthRepository, times(1)).save(user);
    }

    @Test
    void changePassword_Failure_UserNotFound() {
        when(userAuthRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService
                .changePassword("testUser", "oldPass", "newPass"));
    }

    @Test
    void changePassword_Failure_IncorrectOldPassword() {
        String username = "testUser";
        UserAuth user = new UserAuth();
        user.setUsername(username);
        user.setPassword("encodedOldPassword");
        when(userAuthRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPass",
                user.getPassword())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authService.
                changePassword(username, "wrongOldPass", "newPass"));
    }

    @Test
    void deleteUser_Success() {
        Long userId = 1L;
        UserAuth user = new UserAuth();
        user.setId(userId);
        when(userAuthRepository.findById(userId)).thenReturn(Optional.of(user));
        authService.deleteUser(userId);
        verify(userAuthRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_Failure_UserNotFound() {
        when(userAuthRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.deleteUser(1L));
    }
}