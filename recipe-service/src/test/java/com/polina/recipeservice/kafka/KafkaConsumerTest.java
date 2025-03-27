package com.polina.recipeservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.dto.ReviewEvent;
import com.polina.recipeservice.client.UserClient;
import com.polina.recipeservice.elasticsearch.RecipeSearchRepository;
import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.entity.UserRecommendation;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.UserRecommendationRepository;
import com.polina.recipeservice.service.RecipeService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.mockito.Mockito.*;

class KafkaConsumerTest {
    @Mock
    private UserClient userClient;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private UserRecommendationRepository userRecommendationRepository;
    @Mock
    private RecipeSearchRepository recipeSearchRepository;
    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processUserPreferencesUpdate_ValidMessage_ShouldUpdateRecommendations() {
        String message = "User ID: 1";
        Long userId = 1L;
        Map<String, Object> mockResponse = Map.of("ingredients", List.of("tomato", "cheese"));
        Recipe recipe = mock(Recipe.class);
        when(recipe.getProducts()).thenReturn(Collections.emptyList());
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));
        when(userClient.getUserPreferences(userId)).thenReturn(ResponseEntity.ok(mockResponse));
        kafkaConsumer.processUserPreferencesUpdate(message);
        verify(userRecommendationRepository, times(1))
                .save(any(UserRecommendation.class));
    }

    @Test
    void processUserPreferencesUpdate_InvalidMessage_ShouldReturn() {
        kafkaConsumer.processUserPreferencesUpdate("Invalid message");
        verifyNoInteractions(userClient, userRecommendationRepository);
    }

    @Test
    void processUserPreferencesUpdate_UserServiceUnavailable_ShouldReturn() {
        when(userClient.getUserPreferences(anyLong())).thenThrow(FeignException.class);
        kafkaConsumer.processUserPreferencesUpdate("User ID: 1");
        verifyNoInteractions(userRecommendationRepository);
    }

    @Test
    void processReviewEvent_ValidMessage_ShouldUpdateRecipe() throws Exception {
        ReviewEvent event = new ReviewEvent(1L, 4.5);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(event);
        Recipe recipe = mock(Recipe.class);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        kafkaConsumer.processReviewEvent(message);

        verify(recipe).setAverageRating(4.5);
        verify(recipeRepository, times(1)).save(recipe);
    }

    @Test
    void processReviewEvent_InvalidMessage_ShouldHandleError() {
        kafkaConsumer.processReviewEvent("Invalid JSON");
        verifyNoInteractions(recipeRepository);
    }

    @Test
    void handleUserDeleted_ShouldDeleteUserRecipes() {
        kafkaConsumer.handleUserDeleted(1L);
        verify(recipeService, times(1)).deleteRecipesByAuthor(1L);
    }
}