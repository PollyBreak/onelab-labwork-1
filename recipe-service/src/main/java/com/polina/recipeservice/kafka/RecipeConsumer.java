package com.polina.recipeservice.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.recipeservice.dto.*;
import com.polina.recipeservice.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RecipeConsumer {

    @Autowired
    public RecipeService recipeService;

    @Autowired
    public KafkaTemplate<String, String> kafkaTemplate;

    public ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "recipe.create", groupId = "recipe-group")
    public void consumeRecipeCreation(String recipeJson) {
        try {
            Map<String, Object> recipeMap = objectMapper.readValue(recipeJson, new TypeReference<>() {});

            String title = (String) recipeMap.get("title");
            String description = (String) recipeMap.get("description");
            String instructions = (String) recipeMap.get("instructions");
            Long authorId = Long.valueOf(recipeMap.get("authorId").toString());

            List<ProductDTO> products = ((List<String>) recipeMap.get("products")).stream()
                    .map(name -> new ProductDTO(null, name.trim()))
                    .collect(Collectors.toList());

            RecipeDTO recipe = RecipeDTO.builder()
                    .title(title)
                    .description(description)
                    .instructions(instructions)
                    .authorId(authorId)
                    .products(products)
                    .build();

            recipeService.addRecipe(authorId, recipe, products);
            kafkaTemplate.send("recipe.success.response", "Recipe successfully saved: " + title);
        } catch (Exception e) {
            kafkaTemplate.send("recipe.error.response", "Error processing recipe creation: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "recipe.all.request", groupId = "recipe-group")
    public void handleAllRecipesRequest(String request) {
        try {
            List<RecipeDTO> recipes = recipeService.getAllRecipes();
            for (RecipeDTO recipe : recipes) {
                recipe.setAverageRating(recipeService.getAverageRating(recipe.getId()));
            }
            kafkaTemplate.send("recipe.all.response", objectMapper.writeValueAsString(recipes));
        } catch (Exception e) {
            kafkaTemplate.send("recipe.error.response", "Error fetching all recipes: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "recipe.user.request", groupId = "recipe-group")
    public void handleUserRecipeRequest(String userIdJson) {
        try {
            Long userId = objectMapper.readValue(userIdJson, Long.class);
            List<RecipeDTO> recipes = recipeService.getRecipesByUser(userId);

            if (recipes.isEmpty()) {
                kafkaTemplate.send("recipe.user.response", objectMapper.writeValueAsString(Map.of("message", "No recipes found for User ID: " + userId)));
            } else {
                kafkaTemplate.send("recipe.user.response", objectMapper.writeValueAsString(recipes));
            }
        } catch (Exception e) {
            kafkaTemplate.send("recipe.error.response", "Error fetching recipes for user: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "recipe.review", groupId = "recipe-group")
    public void handleReview(String reviewJson) {
        try {
            Map<String, Object> reviewMap = objectMapper.readValue(reviewJson, new TypeReference<>() {});
            Long recipeId = Long.valueOf(reviewMap.get("recipeId").toString());
            Long userId = Long.valueOf(reviewMap.get("userId").toString());
            int rating = Integer.parseInt(reviewMap.get("rating").toString());
            String comment = (String) reviewMap.get("comment");

            recipeService.addReview(recipeId, userId, rating, comment);
            double updatedRating = recipeService.getAverageRating(recipeId);

            kafkaTemplate.send("recipe.success.response", "Review saved for Recipe ID: " + recipeId + ". Updated Rating: " + updatedRating);
        } catch (Exception e) {
            kafkaTemplate.send("recipe.error.response", "Error processing review: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "user.recommendations", groupId = "recipe-group")
    public void handleRecommendationRequest(String userIdJson) {
        try {
            Long userId = objectMapper.readValue(userIdJson, Long.class);
            List<RecipeDTO> recommendedRecipes = recipeService.getRecommendedRecipes(userId);

            for (RecipeDTO recipe : recommendedRecipes) {
                recipe.setAverageRating(recipeService.getAverageRating(recipe.getId()));
            }

            if (recommendedRecipes.isEmpty()) {
                kafkaTemplate.send("user.recommendations.response", objectMapper.writeValueAsString(Map.of("message", "No recommended recipes found.")));
            } else {
                kafkaTemplate.send("user.recommendations.response", objectMapper.writeValueAsString(recommendedRecipes));
            }
        } catch (Exception e) {
            kafkaTemplate.send("recipe.error.response", "Error fetching recommended recipes: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "user.preferences", groupId = "recipe-group")
    public void handleUserPreferences(String preferencesJson) {
        try {
            UserPreferencesDTO preferences = objectMapper.readValue(preferencesJson, UserPreferencesDTO.class);
            recipeService.saveUserPreferences(preferences.getUserId(), preferences.getFavoriteIngredients());
            kafkaTemplate.send("recipe.success.response", "User preferences saved for User ID: " + preferences.getUserId());
        } catch (Exception e) {
            kafkaTemplate.send("recipe.error.response", "Error processing user preferences: " + e.getMessage());
        }
    }
}
