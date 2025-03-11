package com.polina.appservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.appservice.kafka.ConsoleProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class ConsoleProducerTest {
    private ConsoleProducer consoleProducer;
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        consoleProducer = new ConsoleProducer();
        consoleProducer.kafkaTemplate = kafkaTemplate;
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSendUserCreation_valid() throws JsonProcessingException {
        consoleProducer.sendUserCreation("testuser", "test@example.com");
        verify(kafkaTemplate, times(1)).send(eq("user.create"), anyString());
    }

    @Test
    void testSendUserCreation_failure() {
        consoleProducer.sendUserCreation(null, null);
        // no exception should be thrown
    }

    @Test
    void testSendRecipeCreation_valid() throws JsonProcessingException {
        Map<String, Object> recipe = Map.of("title", "Recipe1", "description", "Tasty dish");
        consoleProducer.sendRecipeCreation(recipe);
        verify(kafkaTemplate, times(1)).send(eq("recipe.create"), anyString());
    }

    @Test
    void testSendRecipeCreation_failure() {
        consoleProducer.sendRecipeCreation(null);
    }

    @Test
    void testRequestAllUsers() {
        consoleProducer.requestAllUsers();
        verify(kafkaTemplate, times(1)).send("user.all.request", "fetch");
    }

    @Test
    void testRequestAllRecipes() {
        consoleProducer.requestAllRecipes();
        verify(kafkaTemplate, times(1)).send("recipe.all.request", "fetch");
    }

    @Test
    void testRequestRecipesByUser() throws JsonProcessingException {
        consoleProducer.requestRecipesByUser(123L);
        verify(kafkaTemplate, times(1)).send(eq("recipe.user.request"), anyString());
    }

    @Test
    void testSendRecipeReview() throws JsonProcessingException {
        consoleProducer.sendRecipeReview(1L, 2L, 5, "Great recipe!");
        verify(kafkaTemplate, times(1)).send(eq("recipe.review"), anyString());
    }

    @Test
    void testSendUserPreferences() throws JsonProcessingException {
        consoleProducer.sendUserPreferences(3L, List.of("Tomato", "Cheese"));
        verify(kafkaTemplate, times(1)).send(eq("user.preferences"), anyString());
    }

    @Test
    void testRequestRecommendedRecipes() throws JsonProcessingException {
        consoleProducer.requestRecommendedRecipes(5L);
        verify(kafkaTemplate, times(1)).send(eq("user.recommendations"), anyString());
    }
}
