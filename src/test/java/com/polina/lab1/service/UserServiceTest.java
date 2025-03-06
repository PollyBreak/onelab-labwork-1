package com.polina.lab1.service;

import com.polina.lab1.dto.UserDTO;
import com.polina.lab1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserDTO user;

    @BeforeEach
    void setUp() {
        user = new UserDTO();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
    }

    @Test
    void saveUser_ShouldSaveUser_WhenValid() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveUser_ShouldThrowException_WhenUsernameTaken() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.saveUser(user)
        );
        assertEquals("Username 'testUser' is already taken!", exception.getMessage());
    }

    @Test
    void saveUser_ShouldThrowException_WhenEmailTaken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.saveUser(user)
        );
        assertEquals("Email 'test@example.com' is already registered!", exception.getMessage());
    }

    @Test
    void findUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.findUserById(1L);

        assertEquals(user, result);
    }

    @Test
    void findUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () ->
                userService.findUserById(1L)
        );
        assertEquals("User with ID 1 was not found.", exception.getMessage());
    }

    @Test
    void findUserByUsername_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.findUserByUsername("testUser");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDTO> users = userService.getAllUsers();

        assertEquals(1, users.size());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(NoSuchElementException.class, () ->
                userService.deleteUser(1L)
        );
        assertEquals("User with ID 1 does not exist.", exception.getMessage());
    }
}
