package com.polina.userservice.controller;

import com.polina.userservice.dto.UserPreferencesDTO;
import com.polina.userservice.service.UserPreferencesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferencesControllerTest {

    @InjectMocks
    private UserPreferencesController userPreferencesController;

    @Mock
    private UserPreferencesService userPreferencesService;

    private UserPreferencesDTO mockPreferences;

    @BeforeEach
    void setUp() {
        mockPreferences = new UserPreferencesDTO(1L, List.of("Tomato", "Cheese"));
    }

    @Test
    void testAddUserPreferences() {
        doNothing().when(userPreferencesService).addUserPreferences(mockPreferences);

        ResponseEntity<String> response = userPreferencesController.addUserPreferences(mockPreferences);

        assertEquals("User preferences updated successfully (new ingredients added).", response.getBody());
        verify(userPreferencesService, times(1)).addUserPreferences(mockPreferences);
    }

    @Test
    void testGetUserPreferences() {
        when(userPreferencesService.getUserPreferences(1L)).thenReturn(mockPreferences);

        ResponseEntity<UserPreferencesDTO> response = userPreferencesController.getUserPreferences(1L);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getUserId());
    }

    @Test
    void testRemoveUserPreferences() {
        List<String> ingredientsToRemove = List.of("Tomato");
        doNothing().when(userPreferencesService).removeUserPreferences(1L, ingredientsToRemove);

        ResponseEntity<String> response = userPreferencesController.removeUserPreferences(1L, ingredientsToRemove);

        assertEquals("User preferences updated successfully (ingredients removed).", response.getBody());
        verify(userPreferencesService, times(1)).removeUserPreferences(1L, ingredientsToRemove);
    }
}
