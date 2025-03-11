package com.polina.recipeservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.recipeservice.dto.ProductDTO;
import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.dto.UserPreferencesDTO;
import com.polina.recipeservice.kafka.RecipeConsumer;
import com.polina.recipeservice.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeConsumerTest {

    @InjectMocks
    private RecipeConsumer recipeConsumer;

    @Mock
    private RecipeService recipeService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        recipeConsumer.objectMapper = objectMapper;
    }

    @Test
    void consumeRecipeCreation_Success() throws Exception {
        String json = "{\"title\":\"Pizza\",\"description\":\"Cheesy Pizza\",\"instructions\":\"Bake at 350\",\"authorId\":1,\"products\":[\"Cheese\",\"Tomato\"]}";

        when(objectMapper.readValue(eq(json), any(TypeReference.class))).thenReturn(
                Map.of("title", "Pizza", "description", "Cheesy Pizza", "instructions", "Bake at 350",
                        "authorId", 1, "products", List.of("Cheese", "Tomato"))
        );

        recipeConsumer.consumeRecipeCreation(json);

        verify(recipeService).addRecipe(eq(1L), any(RecipeDTO.class), anyList());
        verify(kafkaTemplate).send(eq("recipe.success.response"), contains("Recipe successfully saved"));
    }

    @Test
    void consumeRecipeCreation_Failure() throws Exception {
        String invalidJson = "INVALID_JSON";

        doThrow(new JsonProcessingException("Invalid JSON") {}).when(objectMapper).readValue(eq(invalidJson), any(TypeReference.class));

        recipeConsumer.consumeRecipeCreation(invalidJson);

        verify(kafkaTemplate).send(eq("recipe.error.response"), contains("Error processing recipe creation"));
    }

    @Test
    void handleAllRecipesRequest_Success() throws Exception {
        List<RecipeDTO> recipes = List.of(new RecipeDTO());
        when(recipeService.getAllRecipes()).thenReturn(recipes);
        when(objectMapper.writeValueAsString(any())).thenReturn("mockedJson");

        recipeConsumer.handleAllRecipesRequest("");

        verify(kafkaTemplate).send(eq("recipe.all.response"), eq("mockedJson"));
    }

    @Test
    void handleAllRecipesRequest_Failure() throws Exception {
        when(recipeService.getAllRecipes()).thenThrow(new RuntimeException("Database error"));

        recipeConsumer.handleAllRecipesRequest("");

        verify(kafkaTemplate).send(eq("recipe.error.response"), contains("Error fetching all recipes"));
    }

    @Test
    void handleUserRecipeRequest_Success() throws Exception {
        String json = "1";
        List<RecipeDTO> recipes = List.of(new RecipeDTO());
        when(objectMapper.readValue(eq(json), eq(Long.class))).thenReturn(1L);
        when(recipeService.getRecipesByUser(1L)).thenReturn(recipes);
        when(objectMapper.writeValueAsString(any())).thenReturn("mockedJson");

        recipeConsumer.handleUserRecipeRequest(json);

        verify(kafkaTemplate).send(eq("recipe.user.response"), eq("mockedJson"));
    }

    @Test
    void handleUserRecipeRequest_Failure() throws Exception {
        String json = "INVALID_JSON";
        doThrow(new JsonProcessingException("Invalid JSON") {}).when(objectMapper).readValue(eq(json), eq(Long.class));

        recipeConsumer.handleUserRecipeRequest(json);

        verify(kafkaTemplate).send(eq("recipe.error.response"), contains("Error fetching recipes for user"));
    }

    @Test
    void handleReview_Success() throws Exception {
        String reviewJson = "{\"recipeId\":1,\"userId\":2,\"rating\":5,\"comment\":\"Great!\"}";
        when(objectMapper.readValue(eq(reviewJson), any(TypeReference.class))).thenReturn(
                Map.of("recipeId", 1, "userId", 2, "rating", 5, "comment", "Great!")
        );
        when(recipeService.getAverageRating(1L)).thenReturn(4.5);

        recipeConsumer.handleReview(reviewJson);

        verify(recipeService).addReview(eq(1L), eq(2L), eq(5), eq("Great!"));
        verify(kafkaTemplate).send(eq("recipe.success.response"), contains("Review saved"));
    }

    @Test
    void handleReview_Failure() throws Exception {
        String invalidJson = "INVALID_JSON";
        doThrow(mock(JsonProcessingException.class)).when(objectMapper).readValue(eq(invalidJson), any(TypeReference.class));

        recipeConsumer.handleReview(invalidJson);

        verify(kafkaTemplate).send(eq("recipe.error.response"), contains("Error processing review"));
    }

    @Test
    void handleRecommendationRequest_Success() throws Exception {
        String json = "1";
        List<RecipeDTO> recommendations = List.of(new RecipeDTO());
        when(objectMapper.readValue(eq(json), eq(Long.class))).thenReturn(1L);
        when(recipeService.getRecommendedRecipes(1L)).thenReturn(recommendations);
        when(objectMapper.writeValueAsString(any())).thenReturn("mockedJson");

        recipeConsumer.handleRecommendationRequest(json);

        verify(kafkaTemplate).send(eq("user.recommendations.response"), eq("mockedJson"));
    }

    @Test
    void handleRecommendationRequest_Failure() throws Exception {
        String json = "INVALID_JSON";
        doThrow(new JsonProcessingException("Invalid JSON") {}).when(objectMapper).readValue(eq(json), eq(Long.class));

        recipeConsumer.handleRecommendationRequest(json);

        verify(kafkaTemplate).send(eq("recipe.error.response"), contains("Error fetching recommended recipes"));
    }

    @Test
    void handleUserPreferences_Success() throws Exception {
        String json = "{\"userId\":1,\"favoriteIngredients\":[\"Cheese\",\"Tomato\"]}";
        UserPreferencesDTO preferences = new UserPreferencesDTO(1L, List.of("Cheese", "Tomato"));
        when(objectMapper.readValue(eq(json), eq(UserPreferencesDTO.class))).thenReturn(preferences);

        recipeConsumer.handleUserPreferences(json);

        verify(recipeService).saveUserPreferences(eq(1L), eq(List.of("Cheese", "Tomato")));
        verify(kafkaTemplate).send(eq("recipe.success.response"), contains("User preferences saved"));
    }

    @Test
    void handleUserPreferences_Failure() throws Exception {
        String json = "INVALID_JSON";
        doThrow(new JsonProcessingException("Invalid JSON") {}).when(objectMapper).readValue(eq(json), eq(UserPreferencesDTO.class));

        recipeConsumer.handleUserPreferences(json);

        verify(kafkaTemplate).send(eq("recipe.error.response"), contains("Error processing user preferences"));
    }
}
