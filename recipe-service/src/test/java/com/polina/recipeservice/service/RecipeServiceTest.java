package com.polina.recipeservice.service;

import com.polina.recipeservice.client.UserClient;
import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.dto.ReviewDTO;
import com.polina.recipeservice.entity.Product;
import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.entity.Review;
import com.polina.recipeservice.repository.ProductRepository;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

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
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService(recipeRepository, productRepository, reviewRepository, userClient);
    }

    @Test
    void testCreateRecipe_Success() {
        RecipeDTO recipeDTO = new RecipeDTO(1L, "Pasta", "Tasty pasta", "Boil water", 1L, List.of("Tomato", "Cheese"), 4.5);
        when(userClient.checkUserExists(1L)).thenReturn(ResponseEntity.ok().build());
        when(productRepository.findByName("Tomato")).thenReturn(null);
        when(productRepository.findByName("Cheese")).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenReturn(new Product(1L, "Tomato"));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(new Recipe());

        assertDoesNotThrow(() -> recipeService.createRecipe(recipeDTO));
    }

    @Test
    void testCreateRecipe_UserNotFound() {
        RecipeDTO recipeDTO = new RecipeDTO(1L, "Pasta", "Tasty pasta", "Boil water", 99L, List.of("Tomato"), 4.5);
        doThrow(new RuntimeException("User not found"))
                .when(userClient).checkUserExists(99L);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> recipeService.createRecipe(recipeDTO));
        assertEquals("User with ID 99 does not exist.", exception.getMessage());
    }

    @Test
    void testGetAllRecipes() {
        List<Recipe> recipes = List.of(new Recipe(1L, "Pasta", "Tasty pasta", "Boil water", 1L, List.of(new Product(1L, "Tomato")), 4.5));
        when(recipeRepository.findAll()).thenReturn(recipes);

        List<RecipeDTO> result = recipeService.getAllRecipes();
        assertEquals(1, result.size());
    }

    @Test
    void testGetRecipesByUser() {
        List<Recipe> recipes = List.of(new Recipe(1L, "Pasta", "Tasty pasta", "Boil water", 1L, List.of(new Product(1L, "Tomato")), 4.5));
        when(recipeRepository.findByAuthorId(1L)).thenReturn(recipes);

        List<RecipeDTO> result = recipeService.getRecipesByUser(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetRecommendedRecipes() {
        Map<String, Object> mockResponse = Map.of("favoriteIngredients", List.of("Tomato"));
        when(userClient.getUserPreferences(1L)).thenReturn(ResponseEntity.ok(mockResponse));
        List<Recipe> recipes = List.of(new Recipe(1L, "Pasta", "Tasty pasta", "Boil water", 1L, List.of(new Product(1L, "Tomato")), 4.5));
        when(recipeRepository.findAll()).thenReturn(recipes);

        List<RecipeDTO> result = recipeService.getRecommendedRecipes(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testAddReview() {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setRating(5);
        reviewDTO.setComment("Great");
        reviewDTO.setUserId(1L);

        Recipe recipe = new Recipe(1L, "Pasta", "Tasty pasta", "Boil water", 1L, List.of(), 4.5);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(reviewRepository.findByRecipeIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> recipeService.addReview(1L, reviewDTO));
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

}
