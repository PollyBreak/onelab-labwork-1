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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {
    @Mock
    private RecipeService recipeService;
    @Mock
    private RecommendationService recommendationService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private RecipeController recipeController;

    private RecipeDTO recipeDTO;
    private ReviewDTO reviewDTO;

    @BeforeEach
    void setUp() {
        recipeDTO = RecipeDTO.builder()
                .id(1L)
                .title("Pasta")
                .description("Delicious pasta")
                .instructions("Boil water")
                .authorId(100L)
                .products(List.of("Tomato"))
                .averageRating(4.5)
                .build();

        reviewDTO = new ReviewDTO(1L, 100L, 5, "Excellent!");
    }

    @Test
    void createRecipe_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(100L);
        SecurityContextHolder.setContext(securityContext);
        ResponseEntity<String> response = recipeController.createRecipe(recipeDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Recipe created successfully", response.getBody());
        verify(recipeService, times(1)).createRecipe(recipeDTO);
    }

    @Test
    void createRecipe_Unauthorized() {
        SecurityContextHolder.clearContext();
        ResponseEntity<String> response = recipeController.createRecipe(recipeDTO);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
        verify(recipeService, never()).createRecipe(any());
    }

    @Test
    void createRecipe_Forbidden() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(200L);
        SecurityContextHolder.setContext(securityContext);
        ResponseEntity<String> response = recipeController.createRecipe(recipeDTO);
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Forbidden: You can only create recipes for yourself", response.getBody());
        verify(recipeService, never()).createRecipe(any());
    }

    @Test
    void getRecipes() {
        when(recipeService.findRecipes(null, null,
                null, null, null)).thenReturn(List.of(recipeDTO));
        ResponseEntity<List<RecipeDTO>> response = recipeController.getRecipes(null,
                null, null, null, null);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Pasta", response.getBody().get(0).getTitle());
    }

    @Test
    void getRecipesByUser() {
        when(recipeService.getRecipesByUser(100L)).thenReturn(List.of(recipeDTO));
        ResponseEntity<List<RecipeDTO>> response = recipeController.getRecipesByUser(100L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getUserRecommendations() {
        when(recommendationService.getUserRecommendations(100L))
                .thenReturn(List.of(recipeDTO));
        ResponseEntity<List<RecipeDTO>> response = recipeController.getUserRecommendations(100L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void addReview() {
        ResponseEntity<String> response = recipeController.addReview(1L, reviewDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Review added successfully", response.getBody());
        verify(recipeService, times(1)).addReview(1L, reviewDTO);
    }

    @Test
    void getRecipesGroupedByCuisine() {
        when(recipeService.groupRecipesByCuisine()).thenReturn(Map.of("Italian", List.of(recipeDTO)));
        ResponseEntity<Map<String, List<RecipeDTO>>> response = recipeController
                .getRecipesGroupedByCuisine();
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("Italian"));
    }

    @Test
    void getRecipesGroupedByProductCount() {
        when(recipeService.groupRecipesByProductCount()).thenReturn(Map.of(1, List.of(recipeDTO)));
        ResponseEntity<Map<Integer, List<RecipeDTO>>> response = recipeController
                .getRecipesGroupedByProductCount();
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey(1));
    }

    @Test
    void getRecipesPartitionedByRating() {
        when(recipeService.partitionRecipesByRating(4.5)).
                thenReturn(Map.of(true, List.of(recipeDTO)));
        ResponseEntity<Map<Boolean, List<RecipeDTO>>> response = recipeController
                .getRecipesPartitionedByRating(4.5);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey(true));
    }

    @Test
    void compareStreamPerformance() {
        when(recipeService.compareSequentialVsParallelProcessing())
                .thenReturn(Map.of("Sequential", 100.0, "Parallel", 50.0));
        ResponseEntity<Map<String, Double>> response = recipeController.compareStreamPerformance();
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("Sequential"));
        assertTrue(response.getBody().containsKey("Parallel"));
    }

    @Test
    void syncRecipes() {
        ResponseEntity<String> response = recipeController.syncRecipes();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Recipes successfully synchronized to Elasticsearch.", response.getBody());
        verify(recipeService, times(1)).syncRecipesToElasticsearch();
    }
}
