package com.polina.recipeservice.kafka;

import com.polina.recipeservice.client.UserClient;
import com.polina.recipeservice.entity.Product;
import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.entity.UserRecommendation;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.UserRecommendationRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Mock
    private UserClient userClient;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRecommendationRepository userRecommendationRepository;

    @BeforeEach
    void setUp() {
        kafkaConsumer = new KafkaConsumer(userClient, recipeRepository, userRecommendationRepository);
    }

    @Test
    void testProcessUserPreferencesUpdate_Success() {
        String message = "User ID: 123";

        List<Product> products = List.of(new Product("tomato"), new Product("cheese"));
        Recipe mockRecipe = new Recipe();
        mockRecipe.setProducts(products);
        mockRecipe.setAverageRating(4.5);

        List<Recipe> mockRecipes = List.of(mockRecipe);
        Map<String, Object> mockResponse = Map.of("favoriteIngredients", List.of("tomato", "cheese"));

        when(userClient.getUserPreferences(123L)).thenReturn(ResponseEntity.ok(mockResponse));
        when(recipeRepository.findAll()).thenReturn(mockRecipes);

        kafkaConsumer.processUserPreferencesUpdate(message);

        verify(userRecommendationRepository, times(1)).save(any(UserRecommendation.class));
    }

    @Test
    void testProcessUserPreferencesUpdate_InvalidMessage() {
        kafkaConsumer.processUserPreferencesUpdate("Invalid message");
        verifyNoInteractions(userClient, recipeRepository, userRecommendationRepository);
    }

    @Test
    void testProcessUserPreferencesUpdate_UserServiceUnavailable() {
        String message = "User ID: 123";
        when(userClient.getUserPreferences(123L)).thenThrow(FeignException.class);

        kafkaConsumer.processUserPreferencesUpdate(message);

        verifyNoInteractions(recipeRepository, userRecommendationRepository);
    }

    @Test
    void testProcessUserPreferencesUpdate_NullResponse() {
        String message = "User ID: 123";
        when(userClient.getUserPreferences(123L)).thenReturn(ResponseEntity.ok(null));

        kafkaConsumer.processUserPreferencesUpdate(message);

        verifyNoInteractions(recipeRepository, userRecommendationRepository);
    }

    @Test
    void testProcessUserPreferencesUpdate_NoFavoriteIngredients() {
        String message = "User ID: 123";
        Map<String, Object> mockResponse = Map.of();
        when(userClient.getUserPreferences(123L)).thenReturn(ResponseEntity.ok(mockResponse));

        kafkaConsumer.processUserPreferencesUpdate(message);

        verifyNoInteractions(recipeRepository, userRecommendationRepository);
    }

    @Test
    void testProcessUserPreferencesUpdate_EmptyRecipeList() {
        String message = "User ID: 123";
        Map<String, Object> mockResponse = Map.of("favoriteIngredients", List.of("tomato", "cheese"));

        when(userClient.getUserPreferences(123L)).thenReturn(ResponseEntity.ok(mockResponse));
        when(recipeRepository.findAll()).thenReturn(List.of());

        kafkaConsumer.processUserPreferencesUpdate(message);

        verify(userRecommendationRepository, times(1)).save(any(UserRecommendation.class));
    }

    @Test
    void testProcessUserPreferencesUpdate_SortingByRating() {
        String message = "User ID: 123";
        List<Product> products = List.of(new Product("tomato"));

        Recipe lowRatedRecipe = new Recipe();
        lowRatedRecipe.setProducts(products);
        lowRatedRecipe.setAverageRating(3.0);

        Recipe highRatedRecipe = new Recipe();
        highRatedRecipe.setProducts(products);
        highRatedRecipe.setAverageRating(5.0);

        List<Recipe> mockRecipes = List.of(lowRatedRecipe, highRatedRecipe);
        Map<String, Object> mockResponse = Map.of("favoriteIngredients", List.of("tomato"));

        when(userClient.getUserPreferences(123L)).thenReturn(ResponseEntity.ok(mockResponse));
        when(recipeRepository.findAll()).thenReturn(mockRecipes);

        kafkaConsumer.processUserPreferencesUpdate(message);

        verify(userRecommendationRepository, times(1)).save(argThat(recommendation ->
                recommendation.getRecommendedRecipes().get(0).getAverageRating() == 5.0));
    }

    @Test
    void testProcessUserPreferencesUpdate_NoRelevantRecipes() {
        String message = "User ID: 123";
        Map<String, Object> mockResponse = Map.of("favoriteIngredients", List.of("banana"));
        when(userClient.getUserPreferences(123L)).thenReturn(ResponseEntity.ok(mockResponse));
        when(recipeRepository.findAll()).thenReturn(List.of());
        kafkaConsumer.processUserPreferencesUpdate(message);
        verify(userRecommendationRepository, times(1)).save(argThat(recommendation ->
                recommendation.getUserId().equals(123L) && recommendation.getRecommendedRecipes().isEmpty()
        ));
    }

    @Test
    void testExtractUserIdFromMessage_Valid() {
        Long userId = kafkaConsumer.extractUserIdFromMessage("User ID: 123");
        assertEquals(123L, userId);
    }

    @Test
    void testExtractUserIdFromMessage_Invalid() {
        Long userId = kafkaConsumer.extractUserIdFromMessage("Invalid message");
        assertNull(userId);
    }

    @Test
    void testExtractUserIdFromMessage_NumberFormatException() {
        Long userId = kafkaConsumer.extractUserIdFromMessage("User: XYZ");
        assertNull(userId);
    }
}
