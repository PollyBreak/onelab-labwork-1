package com.polina.appservice.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ConsoleConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "user.all.response", groupId = "console-group")
    public void receiveAllUsers(ConsumerRecord<String, String> record) {
        try {
            List<Map<String, Object>> users = objectMapper.readValue(record.value(), new TypeReference<>() {});

            if (users.isEmpty()) {
                System.out.println("No users found.");
                return;
            }

            System.out.println("\nAll Users:");
            for (Map<String, Object> user : users) {
                System.out.println("ID: " + user.get("id") + " | Username: " + user.get("username") + " | Email: " + user.get("email"));
            }
        } catch (Exception e) {
            System.err.println("Error parsing user data: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "recipe.all.response", groupId = "console-group")
    public void receiveAllRecipes(String recipesJson) {
        try {
            List<Map<String, Object>> recipes = objectMapper.readValue(recipesJson, new TypeReference<List<Map<String, Object>>>() {});

            if (recipes.isEmpty()) {
                System.out.println("No recipes found.");
                return;
            }

            System.out.println("\nAll Recipes:");
            for (Map<String, Object> recipe : recipes) {
                System.out.println("ID: " + recipe.get("id"));
                System.out.println("Title: " + recipe.get("title"));
                System.out.println("Description: " + recipe.get("description"));
                System.out.println("Instructions: " + recipe.get("instructions"));
                System.out.println("Products: " + recipe.get("products"));
                System.out.println("Rating: " + recipe.get("averageRating"));
                System.out.println("----------------------------------");
            }
        } catch (Exception e) {
            System.err.println("Error parsing all recipes: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "user.recommendations.response", groupId = "console-group")
    public void receiveRecommendedRecipes(String recipesJson) {
        try {
            List<Map<String, Object>> recipes = objectMapper.readValue(recipesJson, new TypeReference<List<Map<String, Object>>>() {});

            if (recipes.isEmpty()) {
                System.out.println("No recommended recipes found.");
                return;
            }

            System.out.println("\nRecommended Recipes (sorted by rating):");
            for (Map<String, Object> recipe : recipes) {
                System.out.println("ID: " + recipe.get("id"));
                System.out.println("Title: " + recipe.get("title"));
                System.out.println("Description: " + recipe.get("description"));
                System.out.println("Instructions: " + recipe.get("instructions"));
                System.out.println("Products: " + recipe.get("products"));
                System.out.println("Rating: " + recipe.get("averageRating"));
                System.out.println("----------------------------------");
            }
        } catch (Exception e) {
            System.err.println("Error parsing recommended recipes: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "recipe.user.response", groupId = "console-group")
    public void receiveUserRecipes(String recipesJson) {
        try {
            List<Map<String, Object>> recipes = objectMapper.readValue(recipesJson, new TypeReference<List<Map<String, Object>>>() {});

            if (recipes.isEmpty()) {
                System.out.println("No recipes found for this user.");
                return;
            }

            System.out.println("\nUser Recipes:");
            for (Map<String, Object> recipe : recipes) {
                System.out.println("ID: " + recipe.get("id"));
                System.out.println("Title: " + recipe.get("title"));
                System.out.println("Description: " + recipe.get("description"));
                System.out.println("Instructions: " + recipe.get("instructions"));
                System.out.println("Products: " + recipe.get("products"));
                System.out.println("Rating: " + recipe.get("averageRating"));
                System.out.println("----------------------------------");
            }
        } catch (Exception e) {
            System.err.println("Error parsing user recipes: " + e.getMessage());
        }
    }


    @KafkaListener(topics = "recipe.error.response", groupId = "console-group")
    public void handleRecipeError(String errorMessage) {
        System.err.println(errorMessage);
    }

    @KafkaListener(topics = "user.error.response", groupId = "console-group")
    public void handleUserError(String errorMessage) {
        System.err.println(errorMessage);
    }

    @KafkaListener(topics = "user.success.response", groupId = "console-group")
    public void handleUserSuccess(String successMessage) {
        System.out.println(successMessage);
    }

    @KafkaListener(topics = "recipe.success.response", groupId = "console-group")
    public void handleRecipeSuccess(String successMessage) {
        System.out.println(successMessage);
    }
}
