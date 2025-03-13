package com.polina.recipeservice.service;

import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.entity.Product;
import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.entity.UserRecommendation;
import com.polina.recipeservice.repository.UserRecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private UserRecommendationRepository userRecommendationRepository;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(userRecommendationRepository);
    }

    @Test
    void testGetUserRecommendations_WithRecommendations() {
        Recipe recipe = new Recipe(1L, "Pasta", "Delicious pasta", "Boil water", 1L, List.of(new Product(1L, "Tomato")), 4.5);
        UserRecommendation userRecommendation = new UserRecommendation(1L, List.of(recipe));

        when(userRecommendationRepository.findById(1L)).thenReturn(Optional.of(userRecommendation));

        List<RecipeDTO> recommendations = recommendationService.getUserRecommendations(1L);

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        assertEquals("Pasta", recommendations.get(0).getTitle());
    }

    @Test
    void testGetUserRecommendations_NoRecommendations() {
        when(userRecommendationRepository.findById(2L)).thenReturn(Optional.empty());
        List<RecipeDTO> recommendations = recommendationService.getUserRecommendations(2L);
        assertNotNull(recommendations);
        assertTrue(recommendations.isEmpty());
    }
}
