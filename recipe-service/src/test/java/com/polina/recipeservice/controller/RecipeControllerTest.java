package com.polina.recipeservice.controller;

import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.dto.ReviewDTO;
import com.polina.recipeservice.service.RecipeService;
import com.polina.recipeservice.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {
    @InjectMocks
    private RecipeController recipeController;
    @Mock
    private RecipeService recipeService;
    @Mock
    private RecommendationService recommendationService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testCreateRecipe_Unauthorized() {
        when(securityContext.getAuthentication()).thenReturn(null);
        ResponseEntity<String> response = recipeController.
                createRecipe(new RecipeDTO(1L, "Test", "Instructions", 1L));
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void testGetAllRecipes() {
        List<RecipeDTO> recipes = List.of(new RecipeDTO(1L,
                "Recipe1", "Instructions", 1L));
        when(recipeService.getAllRecipes()).thenReturn(recipes);
        ResponseEntity<List<RecipeDTO>> response = recipeController.getAllRecipes();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(recipes, response.getBody());
    }

    @Test
    void testGetRecipesByUser() {
        List<RecipeDTO> recipes = List.of(new RecipeDTO(1L,
                "Recipe1", "Instructions", 1L));
        when(recipeService.getRecipesByUser(1L)).thenReturn(recipes);

        ResponseEntity<List<RecipeDTO>> response = recipeController.getRecipesByUser(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(recipes, response.getBody());
    }

    @Test
    void testGetUserRecommendations() {
        List<RecipeDTO> recommendations = List.of(new RecipeDTO(1L,
                "Recommended", "Instructions", 1L));
        when(recommendationService.getUserRecommendations(1L)).thenReturn(recommendations);

        ResponseEntity<List<RecipeDTO>> response = recipeController.getUserRecommendations(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(recommendations, response.getBody());
    }

    @Test
    void testAddReview() {
        ReviewDTO reviewDTO = new ReviewDTO(1, "Great recipe!");
        ResponseEntity<String> response = recipeController.addReview(1L, reviewDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Review added successfully", response.getBody());
        verify(recipeService, times(1)).addReview(1L, reviewDTO);
    }

    @Test
    void testCreateRecipe_Success() {
        RecipeDTO recipeDTO = new RecipeDTO(1L, "Test Recipe",
                "Instructions", 1L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(1L);
        ResponseEntity<String> response = recipeController.createRecipe(recipeDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Recipe created successfully", response.getBody());
        verify(recipeService, times(1)).createRecipe(recipeDTO);
    }

    @Test
    void testCreateRecipe_Forbidden() {
         RecipeDTO recipeDTO = new RecipeDTO(1L, "Test Recipe",
                 "Instructions", 2L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(1L);
        ResponseEntity<String> response = recipeController.createRecipe(recipeDTO);
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Forbidden: You can only create recipes for yourself",
                response.getBody());
    }



}
