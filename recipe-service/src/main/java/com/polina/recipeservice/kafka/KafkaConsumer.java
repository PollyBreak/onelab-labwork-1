package com.polina.recipeservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.recipeservice.client.UserClient;
import com.polina.recipeservice.elasticsearch.RecipeSearchRepository;
import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.entity.UserRecommendation;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.UserRecommendationRepository;
import com.polina.recipeservice.service.RecipeService;
import feign.FeignException;
import com.polina.dto.ReviewEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KafkaConsumer {

    private final UserClient userClient;
    private final RecipeRepository recipeRepository;
    private final UserRecommendationRepository userRecommendationRepository;
    private final RecipeSearchRepository recipeSearchRepository;
    private final RecipeService recipeService;

    public KafkaConsumer(UserClient userClient,
                         RecipeRepository recipeRepository,
                         UserRecommendationRepository userRecommendationRepository,
                         RecipeSearchRepository recipeSearchRepository,
                         RecipeService recipeService) {
        this.userClient = userClient;
        this.recipeRepository = recipeRepository;
        this.userRecommendationRepository = userRecommendationRepository;
        this.recipeSearchRepository = recipeSearchRepository;
        this.recipeService = recipeService;
    }

    @KafkaListener(topics = "user-preferences-topic", groupId = "recipe-service-group")
    @Transactional
    public void processUserPreferencesUpdate(String message) {
        System.out.println("Received Kafka event: " + message);
        Long userId = extractUserIdFromMessage(message);
        if (userId == null) return;

        Map<String, Object> response;
        try {
            response = userClient.getUserPreferences(userId).getBody();
        } catch (FeignException e) {
            System.err.println("User Service is unavailable. Skipping recommendation update.");
            return;
        }
        if (response == null || !response.containsKey("ingredients")) return;
        List<String> favoriteIngredients = (List<String>) response.get("ingredients");
        List<Recipe> recommendedRecipes = recipeRepository.findAll().stream()
                .filter(recipe -> recipe.getProducts().stream()
                        .map(product -> product.getName().toLowerCase())
                        .anyMatch(favoriteIngredients::contains))
                .sorted((r1, r2) -> Double.compare(r2.getAverageRating(), r1.getAverageRating()))
                .collect(Collectors.toList());
        UserRecommendation recommendation = new UserRecommendation(userId, recommendedRecipes);
        userRecommendationRepository.save(recommendation);
        System.out.println("Updated recommendations for User " + userId);
    }

    public Long extractUserIdFromMessage(String message) {
        try {
            return Long.parseLong(message.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            System.err.println("Invalid user ID in Kafka message: " + message);
            return null;
        }
    }


    @KafkaListener(topics = "review-events", groupId = "recipe-service-group")
    @Transactional
    public void processReviewEvent(@Payload String message) {
        //System.out.println("Raw Kafka message: " + message);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ReviewEvent reviewEvent = objectMapper.readValue(message, ReviewEvent.class);
            System.out.println("Deserialized review event: " + reviewEvent);
            updateRecipeRating(reviewEvent.getRecipeId(), reviewEvent.getAverageRating());
            recipeSearchRepository.findById(reviewEvent.getRecipeId().toString())
                            .ifPresent(recipeDocument -> {
                                recipeDocument.setAverageRating(reviewEvent.getAverageRating());
                                recipeSearchRepository.save(recipeDocument);
                            });
        } catch (Exception e) {
            System.err.println("Error deserializing message: " + e.getMessage());
        }
    }


    private void updateRecipeRating(Long recipeId, double newAverageRating) {
        recipeRepository.findById(recipeId).ifPresent(recipe -> {
            recipe.setAverageRating(newAverageRating);
            recipeRepository.save(recipe);
            System.out.println("Updated rating for Recipe ID " + recipeId + ": " + newAverageRating);
        });
    }

    @KafkaListener(topics = "user-deleted-topic", groupId = "recipe-group")
    public void handleUserDeleted(Long userId) {
        System.out.println("Received user deletion event for user ID: " + userId);
        System.out.println("Deleting all recipes for user: " + userId);
        recipeService.deleteRecipesByAuthor(userId);
    }

}
