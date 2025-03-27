package com.polina.userservice.controller;

import com.polina.dto.UserPreferencesDTO;
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
    @Mock
    private UserPreferencesService userPreferencesService;
    @InjectMocks
    private UserPreferencesController userPreferencesController;

    private UserPreferencesDTO userPreferencesDTO;

    @BeforeEach
    void setUp() {
        userPreferencesDTO = UserPreferencesDTO.builder()
                .ingredients(List.of("Tomato", "Cheese"))
                .build();
    }

    @Test
    void shouldReturnUserPreferencesWhenValidUserIdProvided() {
        Long userId = 1L;
        when(userPreferencesService.getUserPreferences(userId)).thenReturn(userPreferencesDTO);
        ResponseEntity<Object> response = userPreferencesController.getUserPreferences(userId);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userPreferencesDTO, response.getBody());
        verify(userPreferencesService, times(1)).getUserPreferences(userId);
    }

    @Test
    void shouldAddUserPreferencesSuccessfully() {
        Long userId = 1L;
        doNothing().when(userPreferencesService).addUserPreferences(userId, userPreferencesDTO);
        ResponseEntity<String> response = userPreferencesController
                .addUserPreferences(userId, userPreferencesDTO);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User preferences updated successfully (new ingredients added).",
                response.getBody());
        verify(userPreferencesService, times(1))
                .addUserPreferences(userId, userPreferencesDTO);
    }

    @Test
    void shouldRemoveUserPreferencesSuccessfully() {
        Long userId = 1L;
        doNothing().when(userPreferencesService).removeUserPreferences(userId, userPreferencesDTO);
        ResponseEntity<String> response = userPreferencesController
                .removeUserPreferences(userId, userPreferencesDTO);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User preferences updated successfully (ingredients removed).",
                response.getBody());
        verify(userPreferencesService, times(1)).removeUserPreferences(userId,
                userPreferencesDTO);
    }
}
