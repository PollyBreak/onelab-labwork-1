package com.polina.appservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConsoleProducer {
    @Autowired
    public KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendUserCreation(String username, String email) {
        try {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("username", username);
            userMap.put("email", email);

            String userJson = objectMapper.writeValueAsString(userMap);
            kafkaTemplate.send("user.create", userJson);
        } catch (Exception e) {
            System.err.println("Error sending user creation request: " + e.getMessage());
        }
    }

    public void sendRecipeCreation(Map<String, Object> recipeMap) {
        try {
            String recipeJson = objectMapper.writeValueAsString(recipeMap);
            kafkaTemplate.send("recipe.create", recipeJson);
        } catch (Exception e) {
            System.err.println("Error sending recipe creation request: " + e.getMessage());
        }
    }


    public void requestAllUsers() {
        kafkaTemplate.send("user.all.request", "fetch");
    }

    public void requestAllRecipes() {
        kafkaTemplate.send("recipe.all.request", "fetch");
    }

    public void requestRecipesByUser(Long userId) {
        try {
            String userIdJson = objectMapper.writeValueAsString(userId);
            kafkaTemplate.send("recipe.user.request", userIdJson);
        } catch (Exception e) {
            System.err.println("Error sending user ID request: " + e.getMessage());
        }
    }

    public void sendRecipeReview(Long recipeId, Long userId, int rating, String comment) {
        try {
            Map<String, Object> review = Map.of(
                    "recipeId", recipeId,
                    "userId", userId,
                    "rating", rating,
                    "comment", comment
            );
            String reviewJson = objectMapper.writeValueAsString(review);
            kafkaTemplate.send("recipe.review", reviewJson);
        } catch (Exception e) {
            System.err.println("Error sending review request: " + e.getMessage());
        }
    }


    public void sendUserPreferences(Long userId, List<String> ingredients) {
        try {
            Map<String, Object> preferences = new HashMap<>();
            preferences.put("userId", userId);
            preferences.put("favoriteIngredients", ingredients);

            String preferencesJson = objectMapper.writeValueAsString(preferences);
            kafkaTemplate.send("user.preferences", preferencesJson);
        } catch (Exception e) {
            System.err.println("Error sending user preferences request: " + e.getMessage());
        }
    }

    public void requestRecommendedRecipes(Long userId) {
        try {
            String userIdJson = objectMapper.writeValueAsString(userId);
            kafkaTemplate.send("user.recommendations", userIdJson);
        } catch (Exception e) {
            System.err.println("Error sending recommendation request: " + e.getMessage());
        }
    }

}
