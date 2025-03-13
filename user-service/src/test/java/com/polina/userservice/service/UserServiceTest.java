package com.polina.userservice.service;

import com.polina.userservice.dto.UserDTO;
import com.polina.userservice.entity.User;
import com.polina.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
    }

    @Test
    void testRegisterUser_Success() {
        UserDTO userDTO = new UserDTO(1L, "testUser", "test@example.com", "password123");
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        String response = userService.registerUser(userDTO);
        assertEquals("User registered successfully!", response);
    }

    @Test
    void testRegisterUser_UsernameTaken() {
        UserDTO userDTO = new UserDTO(1L, "testUser", "test@example.com", "password123");
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(mockUser));

        Exception exception = assertThrows(RuntimeException.class, () -> userService.registerUser(userDTO));
        assertEquals("Username already taken!", exception.getMessage());
    }

    @Test
    void testFindUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserDTO result = userService.findUserById(1L);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getUsername(), result.getUsername());
    }

    @Test
    void testFindUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.findUserById(99L));
        assertEquals("User with ID 99 was not found.", exception.getMessage());
    }

    @Test
    void testGetUserByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        UserDTO result = userService.getUserByUsername("testUser");
        assertEquals(mockUser.getUsername(), result.getUsername());
    }

    @Test
    void testGetUserByUsername_NotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserByUsername("unknownUser"));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        List<UserDTO> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getUsername());
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(99L));
        assertEquals("User with ID 99 does not exist.", exception.getMessage());
    }

    @Test
    void testGetUserIdByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        Long userId = userService.getUserIdByUsername("testUser");
        assertEquals(1L, userId);
    }

    @Test
    void testGetUserIdByUsername_NotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.getUserIdByUsername("unknownUser"));
        assertEquals("User not found", exception.getMessage());
    }
}
