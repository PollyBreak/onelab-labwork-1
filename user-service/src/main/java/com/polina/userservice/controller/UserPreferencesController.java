package com.polina.userservice.controller;


import com.polina.userservice.service.UserPreferencesService;
import com.polina.dto.UserPreferencesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/users/preferences")
public class UserPreferencesController {

    @Autowired
    private UserPreferencesService userPreferencesService;


    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserPreferences(@PathVariable Long userId) {
        return ResponseEntity.ok(userPreferencesService.getUserPreferences(userId));
    }

    @PostMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal or hasAuthority('ADMIN')")
    public ResponseEntity<String> addUserPreferences(@PathVariable Long userId,
                                                     @RequestBody UserPreferencesDTO preferencesDTO) {
        userPreferencesService.addUserPreferences(userId, preferencesDTO);
        return ResponseEntity.ok("User preferences updated successfully (new ingredients added).");
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal or hasAuthority('ADMIN')")
    public ResponseEntity<String> removeUserPreferences(@PathVariable Long userId,
                                                        @RequestBody UserPreferencesDTO preferencesDTO) {
        userPreferencesService.removeUserPreferences(userId, preferencesDTO);
        return ResponseEntity.ok("User preferences updated successfully (ingredients removed).");
    }
}
