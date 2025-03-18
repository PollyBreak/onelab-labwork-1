package com.polina.recipeservice.service;

import com.polina.recipeservice.client.UserClient;
import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.dto.ReviewDTO;
import com.polina.recipeservice.elasticsearch.RecipeDocument;
import com.polina.recipeservice.elasticsearch.RecipeSearchRepository;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RecipeSearchRepository recipeSearchRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe recipe;
    private RecipeDTO recipeDTO;
    private Product product;
    private Review review;
    private ReviewDTO reviewDTO;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Tomato");
        recipe = new Recipe(1L, "Pasta", "Tasty pasta", "Boil water", 100L, "Italian",
                List.of(product), 5.0, LocalDateTime.now());

        recipeDTO = new RecipeDTO(1L, "Pasta", "Tasty pasta", "Boil water", 100L,
                List.of("Tomato"), 5.0, "Italian", LocalDateTime.now());
        review = new Review(1L, 1L, 100L, 5, "Delicious!");
        reviewDTO = new ReviewDTO(1L, 100L, 5, "Delicious!");
    }

    @Test
    void createRecipe_Success() {
        when(userClient.checkUserExists(anyLong())).thenReturn(ResponseEntity.ok().build());
        when(productRepository.findByName(anyString())).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe savedRecipe = invocation.getArgument(0);
            savedRecipe.setId(1L);
            return savedRecipe;
        });
        recipeService.createRecipe(recipeDTO);

        verify(userClient, times(1)).checkUserExists(100L);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
        verify(recipeSearchRepository, times(1)).save(any(RecipeDocument.class));
    }

    @Test
    void createRecipe_UserNotFound_ThrowsException() {
        doThrow(new RuntimeException("User not found")).when(userClient).checkUserExists(100L);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recipeService.createRecipe(recipeDTO));

        assertEquals("User with ID 100 does not exist.", exception.getMessage());
    }

    @Test
    void getAllRecipes_ReturnsList() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        List<RecipeDTO> result = recipeService.getAllRecipes();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Pasta", result.get(0).getTitle());
    }

    @Test
    void getRecipesByUser_ReturnsList() {
        when(recipeRepository.findByAuthorId(100L)).thenReturn(List.of(recipe));

        List<RecipeDTO> result = recipeService.getRecipesByUser(100L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Pasta", result.get(0).getTitle());
    }

    @Test
    void addReview_NewReview_Success() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(reviewRepository.findByRecipeIdAndUserId(1L, 100L)).thenReturn(Optional.empty());

        recipeService.addReview(1L, reviewDTO);

        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void addReview_UpdateExistingReview() {
        Review existingReview = new Review(1L, 1L, 100L, 4, "Good");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(reviewRepository.findByRecipeIdAndUserId(1L, 100L)).thenReturn(Optional.of(existingReview));

        recipeService.addReview(1L, reviewDTO);

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void syncRecipesToElasticsearch() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        recipeService.syncRecipesToElasticsearch();

        verify(recipeSearchRepository, times(1)).saveAll(any());
    }

    @Test
    void findRecipes_FilteredResults() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        List<RecipeDTO> result = recipeService.findRecipes(100L, "Italian",
                List.of("Tomato"), 4.0, LocalDateTime.now().minusDays(1));

        assertEquals(1, result.size());
        assertEquals("Pasta", result.get(0).getTitle());
    }

    @Test
    void groupRecipesByCuisine() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        Map<String, List<RecipeDTO>> result = recipeService.groupRecipesByCuisine();

        assertTrue(result.containsKey("Italian"));
        assertEquals(1, result.get("Italian").size());
    }

    @Test
    void partitionRecipesByRating() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        Map<Boolean, List<RecipeDTO>> result = recipeService.partitionRecipesByRating(4.5);

        assertTrue(result.get(true).contains(recipeService.convertToDTO(recipe)));
    }

    @Test
    void groupRecipesByProductCount() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        Map<Integer, List<RecipeDTO>> result = recipeService.groupRecipesByProductCount();

        assertTrue(result.containsKey(1));
        assertEquals(1, result.get(1).size());
    }

    @Test
    void compareSequentialVsParallelProcessing() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        Map<String, Double> result = recipeService.compareSequentialVsParallelProcessing();

        assertTrue(result.containsKey("Sequential Execution Time (ms)"));
        assertTrue(result.containsKey("Parallel Execution Time (ms)"));
    }
}
