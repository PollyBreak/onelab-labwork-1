package com.polina.userservice.service;

import com.polina.dto.AuthRequest;
import com.polina.dto.UserDTO;
import com.polina.userservice.client.AuthClient;
import com.polina.userservice.entity.User;
import com.polina.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthClient authClient;
    @Mock
    private KafkaTemplate<String, Long> kafkaTemplate;

    @Test
    void registerUser_ShouldRegisterSuccessfully_WhenUsernameIsUnique() {
        AuthRequest request = AuthRequest.builder()
                .username("testUser")
                .password("password")
                .build();
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        String result = userService.registerUser(request);
        verify(authClient).register(any(AuthRequest.class));
        verify(userRepository).save(any(User.class));
        assertEquals("User registered successfully!", result);
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        AuthRequest request = AuthRequest.builder()
                .username("existingUser")
                .password("password")
                .build();
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));
        Exception exception = assertThrows(RuntimeException.class, ()
                -> userService.registerUser(request));
        assertEquals("Username already taken!", exception.getMessage());
    }

    @Test
    void findUserById_ShouldReturnUserDTO_WhenUserExists() {
        User user = User.builder()
                .id(1L)
                .username("testUser")
                .favoriteIngredients(List.of("Tomato", "Cheese"))
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDTO result = userService.findUserById(1L);
        assertEquals(1L, result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals(List.of("Tomato", "Cheese"), result.getFavouriteIngredients());
    }

    @Test
    void findUserById_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.findUserById(99L));
        assertEquals("User with ID 99 was not found.", exception.getMessage());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserDTOs() {
        List<User> users = List.of(
                User.builder().id(1L).username("Alice").favoriteIngredients(List.of("Milk", "Sugar")).build(),
                User.builder().id(2L).username("Bob").favoriteIngredients(List.of("Flour", "Eggs")).build()
        );
        when(userRepository.findAll()).thenReturn(users);
        List<UserDTO> result = userService.getAllUsers();
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getUsername());
        assertEquals("Bob", result.get(1).getUsername());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        Long userId = 10L;
        String token = "Bearer token123";
        when(userRepository.existsById(userId)).thenReturn(true);
        userService.deleteUser(userId, token);
        verify(authClient).deleteUser(token, userId);
        verify(userRepository).deleteById(userId);
        verify(kafkaTemplate).send("user-deleted-topic", userId);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.existsById(50L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(50L, "Bearer token"));
        assertEquals("User with ID 50 does not exist.", exception.getMessage());
        verify(authClient, never()).deleteUser(anyString(), anyLong());
        verify(userRepository, never()).deleteById(anyLong());
        verify(kafkaTemplate, never()).send(anyString(), anyLong());
    }
}
