package com.polina.recipeservice;


import com.polina.recipeservice.dto.ProductDTO;
import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.dto.ReviewDTO;
import com.polina.recipeservice.dto.UserPreferencesDTO;
import com.polina.recipeservice.repository.ProductRepository;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.ReviewRepository;
import com.polina.recipeservice.repository.UserPreferencesRepository;
import com.polina.recipeservice.service.RecipeService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserPreferencesRepository preferencesRepository;

    private RecipeDTO mockRecipe;
    private ReviewDTO mockReview;

    @BeforeEach
    void setUp() {
        mockRecipe = RecipeDTO.builder()
                .id(1L)
                .title("Pizza")
                .description("Cheesy Pizza")
                .instructions("Bake at 350")
                .authorId(1L)
                .products(List.of(ProductDTO.builder().name("Cheese").build()))
                .averageRating(0.0)
                .build();

        mockReview = ReviewDTO.builder()
                .recipeId(1L)
                .userId(2L)
                .rating(5)
                .comment("Delicious!")
                .build();
    }

    @Test
    void addReview_ShouldSaveReviewAndUpdateRating() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(mockRecipe));
        when(reviewRepository.findByRecipeId(1L)).thenReturn(List.of(mockReview));

        recipeService.addReview(1L, 2L, 5, "Delicious!");

        verify(reviewRepository).save(any(ReviewDTO.class));
        verify(recipeRepository).findById(1L);
    }

    @Test
    void addReview_ShouldThrowException_WhenRecipeNotFound() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> recipeService.addReview(1L, 2L, 5, "Delicious!")
        );

        assertEquals("Recipe not found with ID: 1", exception.getMessage());
    }

    @Test
    void getAverageRating_ShouldReturnZero_WhenNoReviews() {
        when(reviewRepository.findByRecipeId(1L)).thenReturn(Collections.emptyList());

        double rating = recipeService.getAverageRating(1L);

        assertEquals(0.0, rating);
    }

    @Test
    void getAverageRating_ShouldReturnCorrectAverage() {
        ReviewDTO review2 = ReviewDTO.builder().recipeId(1L).userId(3L).rating(3).comment("Okay").build();
        when(reviewRepository.findByRecipeId(1L)).thenReturn(List.of(mockReview, review2));

        double rating = recipeService.getAverageRating(1L);

        assertEquals(4.0, rating);
    }

    @Test
    void saveUserPreferences_ShouldSavePreferences() {
        List<String> ingredients = List.of("Cheese", "Tomato");
        recipeService.saveUserPreferences(1L, ingredients);

        verify(preferencesRepository).save(any(UserPreferencesDTO.class));
    }

    @Test
    void getRecommendedRecipes_ShouldReturnEmptyList_WhenNoPreferences() {
        when(preferencesRepository.findById(1L)).thenReturn(Optional.empty());

        List<RecipeDTO> recommendedRecipes = recipeService.getRecommendedRecipes(1L);

        assertTrue(recommendedRecipes.isEmpty());
    }

    @Test
    void getRecommendedRecipes_ShouldReturnMatchingRecipes() {
        UserPreferencesDTO preferences = new UserPreferencesDTO(1L, List.of("Cheese", "Tomato"));
        when(preferencesRepository.findById(1L)).thenReturn(Optional.of(preferences));
        when(recipeRepository.getAllRecipes()).thenReturn(List.of(mockRecipe));

        List<RecipeDTO> recommendedRecipes = recipeService.getRecommendedRecipes(1L);

        assertFalse(recommendedRecipes.isEmpty());
        assertEquals(1, recommendedRecipes.size());
        assertEquals("Pizza", recommendedRecipes.get(0).getTitle());
    }

    @Test
    void getAllRecipes_ShouldReturnRecipesWithRatings() {
        when(recipeRepository.getAllRecipes()).thenReturn(List.of(mockRecipe));
        when(reviewRepository.findByRecipeId(1L)).thenReturn(List.of(mockReview));

        List<RecipeDTO> recipes = recipeService.getAllRecipes();

        assertFalse(recipes.isEmpty());
        assertEquals("Pizza", recipes.get(0).getTitle());
        assertEquals(5.0, recipes.get(0).getAverageRating());
    }

    @Test
    void getRecipesByUser_ShouldReturnRecipes() {
        when(recipeRepository.findByAuthorId(1L)).thenReturn(List.of(mockRecipe));

        List<RecipeDTO> recipes = recipeService.getRecipesByUser(1L);

        assertEquals(1, recipes.size());
        assertEquals("Pizza", recipes.get(0).getTitle());
    }

    @Test
    void addRecipe_ShouldSaveRecipeWithProducts() {
        ProductDTO product = ProductDTO.builder().name("Tomato").build();
        List<ProductDTO> products = List.of(product);

        when(productRepository.findByName("Tomato")).thenReturn(null);
        when(productRepository.save(product)).thenReturn(product);
        when(recipeRepository.save(any(RecipeDTO.class))).thenReturn(mockRecipe);

        recipeService.addRecipe(1L, mockRecipe, products);

        verify(productRepository).save(product);
        verify(recipeRepository).save(any(RecipeDTO.class));
    }

    @Test
    void addRecipe_ShouldUseExistingProducts() {
        ProductDTO product = ProductDTO.builder().name("Cheese").build();
        List<ProductDTO> products = List.of(product);

        when(productRepository.findByName("Cheese")).thenReturn(product);
        when(recipeRepository.save(any(RecipeDTO.class))).thenReturn(mockRecipe);

        recipeService.addRecipe(1L, mockRecipe, products);

        verify(productRepository, never()).save(product);
        verify(recipeRepository).save(any(RecipeDTO.class));
    }
}
