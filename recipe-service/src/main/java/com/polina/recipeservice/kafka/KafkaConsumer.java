package com.polina.recipeservice.kafka;

import com.polina.recipeservice.client.UserClient;
import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.entity.UserRecommendation;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.UserRecommendationRepository;
import feign.FeignException;
import org.springframework.kafka.annotation.KafkaListener;
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

    public KafkaConsumer(UserClient userClient, RecipeRepository recipeRepository,
                         UserRecommendationRepository userRecommendationRepository) {
        this.userClient = userClient;
        this.recipeRepository = recipeRepository;
        this.userRecommendationRepository = userRecommendationRepository;
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

        if (response == null || !response.containsKey("favoriteIngredients")) return;
        List<String> favoriteIngredients = (List<String>) response.get("favoriteIngredients");

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
}
