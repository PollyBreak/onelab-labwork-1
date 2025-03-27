package com.polina.userservice.service;

import com.polina.dto.UserPreferencesDTO;
import com.polina.userservice.entity.User;
import com.polina.userservice.kafka.KafkaProducer;
import com.polina.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferencesServiceTest {
    @InjectMocks
    private UserPreferencesService userPreferencesService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private KafkaProducer kafkaProducer;

    @Test
    void addUserPreferences_ShouldAddPreferences_WhenUserExists() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .username("testUser")
                .favoriteIngredients(new ArrayList<>(List.of("Tomato")))
                .build();
        UserPreferencesDTO preferencesDTO = UserPreferencesDTO.builder()
                .ingredients(List.of("Cheese", "Basil"))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userPreferencesService.addUserPreferences(userId, preferencesDTO);
        assertEquals(3, user.getFavoriteIngredients().size());
        assertTrue(user.getFavoriteIngredients().containsAll(List.of("Tomato", "Cheese", "Basil")));
        verify(userRepository).save(user);
        verify(kafkaProducer).sendUserPreferencesUpdate(userId);
    }

    @Test
    void addUserPreferences_ShouldThrowException_WhenUserDoesNotExist() {
        Long userId = 99L;
        UserPreferencesDTO preferencesDTO = UserPreferencesDTO.builder()
                .ingredients(List.of("Salt"))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userPreferencesService.addUserPreferences(userId, preferencesDTO));
        assertEquals("User with ID 99 does not exist.", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(kafkaProducer, never()).sendUserPreferencesUpdate(anyLong());
    }

    @Test
    void removeUserPreferences_ShouldRemovePreferences_WhenUserExists() {
        Long userId = 2L;
        User user = User.builder()
                .id(userId)
                .username("testUser2")
                .favoriteIngredients(new ArrayList<>(List.of("Salt", "Pepper", "Garlic")))
                .build();
        UserPreferencesDTO preferencesDTO = UserPreferencesDTO.builder()
                .ingredients(List.of("Garlic", "Pepper"))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userPreferencesService.removeUserPreferences(userId, preferencesDTO);
        assertEquals(1, user.getFavoriteIngredients().size());
        assertEquals(List.of("Salt"), user.getFavoriteIngredients());
        verify(userRepository).save(user);
        verify(kafkaProducer).sendUserPreferencesUpdate(userId);
    }

    @Test
    void removeUserPreferences_ShouldThrowException_WhenUserDoesNotExist() {
        Long userId = 100L;
        UserPreferencesDTO preferencesDTO = UserPreferencesDTO.builder()
                .ingredients(List.of("Onion"))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userPreferencesService.removeUserPreferences(userId, preferencesDTO));
        assertEquals("User with ID 100 does not exist.", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(kafkaProducer, never()).sendUserPreferencesUpdate(anyLong());
    }

    @Test
    void getUserPreferences_ShouldReturnPreferences_WhenUserExists() {
        Long userId = 3L;
        User user = User.builder()
                .id(userId)
                .username("testUser3")
                .favoriteIngredients(List.of("Milk", "Butter"))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserPreferencesDTO result = userPreferencesService.getUserPreferences(userId);
        assertEquals(List.of("Milk", "Butter"), result.getIngredients());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserPreferences_ShouldThrowException_WhenUserDoesNotExist() {
        Long userId = 101L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userPreferencesService.getUserPreferences(userId));
        assertEquals("User with ID 101 does not exist.", exception.getMessage());
        verify(userRepository).findById(userId);
    }
}
