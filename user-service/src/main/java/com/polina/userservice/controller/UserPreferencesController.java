package com.polina.userservice.controller;

import com.polina.userservice.dto.UserPreferencesDTO;
import com.polina.userservice.service.UserPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/preferences")
public class UserPreferencesController {

    @Autowired
    private UserPreferencesService userPreferencesService;

    @PostMapping
    public ResponseEntity<String> addUserPreferences(@RequestBody UserPreferencesDTO preferencesDTO) {
        userPreferencesService.addUserPreferences(preferencesDTO);
        return ResponseEntity.ok("User preferences updated successfully (new ingredients added).");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserPreferencesDTO> getUserPreferences(@PathVariable Long userId) {
        return ResponseEntity.ok(userPreferencesService.getUserPreferences(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> removeUserPreferences(@PathVariable Long userId, @RequestBody List<String> ingredientsToRemove) {
        userPreferencesService.removeUserPreferences(userId, ingredientsToRemove);
        return ResponseEntity.ok("User preferences updated successfully (ingredients removed).");
    }
}
