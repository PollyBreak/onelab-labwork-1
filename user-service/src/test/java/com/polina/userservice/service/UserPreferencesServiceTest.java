package com.polina.userservice.service;

import com.polina.userservice.dto.UserPreferencesDTO;
import com.polina.userservice.entity.User;
import com.polina.userservice.kafka.KafkaProducer;
import com.polina.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFavoriteIngredients(new ArrayList<>(List.of("Tomato", "Cheese")));
    }

    @Test
    void testAddUserPreferences_Success() {
        UserPreferencesDTO preferencesDTO = new UserPreferencesDTO(1L, List.of("Onion"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        assertDoesNotThrow(() -> userPreferencesService.addUserPreferences(preferencesDTO));
        verify(userRepository, times(1)).save(any(User.class));
        verify(kafkaProducer, times(1)).sendUserPreferencesUpdate(1L);
    }

    @Test
    void testAddUserPreferences_UserNotFound() {
        UserPreferencesDTO preferencesDTO = new UserPreferencesDTO(99L, List.of("Onion"));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userPreferencesService.addUserPreferences(preferencesDTO));
        assertEquals("User with ID 99 does not exist.", exception.getMessage());
    }

    @Test
    void testRemoveUserPreferences_Success() {
        List<String> ingredientsToRemove = List.of("Tomato");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        assertDoesNotThrow(() -> userPreferencesService
                .removeUserPreferences(1L, ingredientsToRemove));
        verify(userRepository, times(1)).save(any(User.class));
        verify(kafkaProducer, times(1)).sendUserPreferencesUpdate(1L);
    }

    @Test
    void testRemoveUserPreferences_UserNotFound() {
        List<String> ingredientsToRemove = List.of("Tomato");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userPreferencesService.removeUserPreferences(99L, ingredientsToRemove));
        assertEquals("User with ID 99 does not exist.", exception.getMessage());
    }

    @Test
    void testGetUserPreferences_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        UserPreferencesDTO result = userPreferencesService.getUserPreferences(1L);
        assertEquals(1L, result.getUserId());
        assertEquals(2, result.getFavoriteIngredients().size());
    }

    @Test
    void testGetUserPreferences_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userPreferencesService.getUserPreferences(99L));
        assertEquals("User with ID 99 does not exist.", exception.getMessage());
    }
}
