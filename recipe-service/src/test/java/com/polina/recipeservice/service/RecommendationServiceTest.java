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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private UserRecommendationRepository userRecommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private Recipe recipe;
    private RecipeDTO expectedRecipeDTO;
    private UserRecommendation userRecommendation;

    @BeforeEach
    void setUp() {
        recipe = new Recipe(1L, "Pasta", "Delicious pasta", "Boil water", 100L, "Italian",
                List.of(new Product(1L, "Tomato")), 4.5, null);

        expectedRecipeDTO = RecipeDTO.builder()
                .id(1L)
                .title("Pasta")
                .description("Delicious pasta")
                .instructions("Boil water")
                .authorId(100L)
                .products(List.of("Tomato"))
                .averageRating(4.5)
                .build();

        userRecommendation = new UserRecommendation(1L, List.of(recipe));
    }

    @Test
    void getUserRecommendations_UserHasRecommendations() {
        when(userRecommendationRepository.findById(1L)).thenReturn(Optional.of(userRecommendation));

        List<RecipeDTO> result = recommendationService.getUserRecommendations(1L);

        assertEquals(1, result.size());
        assertEquals(expectedRecipeDTO, result.get(0));
    }

    @Test
    void getUserRecommendations_UserHasNoRecommendations() {
        when(userRecommendationRepository.findById(1L)).thenReturn(Optional.empty());

        List<RecipeDTO> result = recommendationService.getUserRecommendations(1L);

        assertTrue(result.isEmpty());
    }
}
