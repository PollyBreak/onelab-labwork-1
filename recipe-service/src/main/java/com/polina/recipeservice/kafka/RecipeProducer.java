package com.polina.recipeservice.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RecipeProducer {
    @Autowired
    public KafkaTemplate<String, String> kafkaTemplate;

    public ObjectMapper objectMapper = new ObjectMapper();

    public void sendRecipeCreated(Object recipe) {
        try {
            String recipeJson = objectMapper.writeValueAsString(recipe);
            kafkaTemplate.send("recipe.created", recipeJson);
        } catch (Exception e) {
            System.err.println("Error sending recipe creation event: " + e.getMessage());
        }
    }


}
