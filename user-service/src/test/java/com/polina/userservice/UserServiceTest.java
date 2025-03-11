package com.polina.userservice;

import com.polina.userservice.dto.UserDTO;
import com.polina.userservice.repository.UserRepository;
import com.polina.userservice.service.UserService;
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

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDTO();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
    }

    @Test
    void saveUser_Success() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());

        userService.saveUser(testUser);

        verify(userRepository).save(testUser);
    }

    @Test
    void saveUser_Failure_DuplicateUsername() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.saveUser(testUser));
        assertEquals("Username 'testUser' is already taken!", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_Failure_DuplicateEmail() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.saveUser(testUser));
        assertEquals("Email 'test@example.com' is already registered!", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void findUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDTO foundUser = userService.findUserById(1L);
        assertEquals(testUser, foundUser);
    }

    @Test
    void findUserById_Failure_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> userService.findUserById(1L));
        assertEquals("User with ID 1 was not found.", exception.getMessage());
    }

    @Test
    void findUserByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        Optional<UserDTO> foundUser = userService.findUserByUsername("testUser");
        assertTrue(foundUser.isPresent());
        assertEquals(testUser, foundUser.get());
    }

    @Test
    void findUserByUsername_Failure_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        Optional<UserDTO> foundUser = userService.findUserByUsername("testUser");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void getAllUsers_Success() {
        List<UserDTO> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> retrievedUsers = userService.getAllUsers();
        assertEquals(1, retrievedUsers.size());
        assertEquals(testUser, retrievedUsers.get(0));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_Failure_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(NoSuchElementException.class, () -> userService.deleteUser(1L));
        assertEquals("User with ID 1 does not exist.", exception.getMessage());
    }
}