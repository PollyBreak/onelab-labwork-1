package com.polina.recipeservice.service;


import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.entity.UserRecommendation;
import com.polina.recipeservice.repository.UserRecommendationRepository;
import com.polina.dto.RecipeDTO;
import com.polina.recipeservice.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {
    @Mock
    private UserRecommendationRepository userRecommendationRepository;
    @InjectMocks
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserRecommendations_UserHasRecommendations_ReturnsRecipeDTOList() {
        Long userId = 1L;
        Recipe recipe = createMockRecipe();
        UserRecommendation userRecommendation = new UserRecommendation();
        userRecommendation.setRecommendedRecipes(List.of(recipe));
        when(userRecommendationRepository.findById(userId))
                .thenReturn(Optional.of(userRecommendation));
        List<RecipeDTO> result = recommendationService.getUserRecommendations(userId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(recipe.getId(), result.get(0).getId());
        assertEquals(recipe.getTitle(), result.get(0).getTitle());
        assertEquals(recipe.getDescription(), result.get(0).getDescription());
        assertEquals(recipe.getInstructions(), result.get(0).getInstructions());
        assertEquals(recipe.getAuthorId(), result.get(0).getAuthorId());
        assertEquals(recipe.getProducts().size(), result.get(0).getProducts().size());
        verify(userRecommendationRepository, times(1)).findById(userId);
    }

    @Test
    void getUserRecommendations_UserHasNoRecommendations_ReturnsEmptyList() {
        Long userId = 2L;
        when(userRecommendationRepository.findById(userId)).thenReturn(Optional.empty());
        List<RecipeDTO> result = recommendationService.getUserRecommendations(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRecommendationRepository, times(1)).findById(userId);
    }

    private Recipe createMockRecipe() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pasta Carbonara");
        recipe.setDescription("A classic Italian pasta dish");
        recipe.setInstructions("Boil pasta. Cook pancetta. Mix with eggs and cheese.");
        recipe.setAuthorId(10L);
        recipe.setProducts(List.of(new Product("Pasta"), new Product("Eggs"), new Product("Pancetta")));
        recipe.setAverageRating(4.5);
        return recipe;
    }
}
