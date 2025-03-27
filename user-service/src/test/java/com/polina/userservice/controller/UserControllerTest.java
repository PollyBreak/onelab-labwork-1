package com.polina.userservice.controller;

import com.polina.dto.AuthRequest;
import com.polina.dto.UserDTO;
import com.polina.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private AuthRequest authRequest;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("password123");
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testUser");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userService.registerUser(authRequest)).thenReturn("User registered successfully");
        String response = userController.registerUser(authRequest);
        assertNotNull(response);
        assertEquals("User registered successfully", response);
        verify(userService, times(1)).registerUser(authRequest);
    }

    @Test
    void shouldReturnUserWhenUserIdIsValid() {
        Long userId = 1L;
        when(userService.findUserById(userId)).thenReturn(userDTO);
        ResponseEntity<Object> response = userController.getUserById(userId);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    void shouldReturnAllUsersSuccessfully() {
        when(userService.getAllUsers()).thenReturn(List.of(userDTO));
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("testUser", response.getBody().get(0).getUsername());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void shouldDeleteUserSuccessfullyWhenValidTokenIsProvided() {
        Long userId = 1L;
        String token = "Bearer sample_token";
        doNothing().when(userService).deleteUser(userId, token);
        ResponseEntity<String> response = userController.deleteUser(userId, token);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User deleted successfully", response.getBody());
        verify(userService, times(1)).deleteUser(userId, token);
    }
}