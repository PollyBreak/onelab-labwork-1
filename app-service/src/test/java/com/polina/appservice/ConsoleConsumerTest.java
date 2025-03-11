package com.polina.appservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.appservice.kafka.ConsoleConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ConsoleConsumerTest {
    private ConsoleConsumer consoleConsumer;
    private ObjectMapper objectMapper;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    void setUp() {
        consoleConsumer = new ConsoleConsumer();
        objectMapper = new ObjectMapper();
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    private String captureOutput() {
        return outputStreamCaptor.toString();
    }

    @Test
    void testReceiveAllUsers_validJson() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(List.of(
                Map.of("id", 1, "username", "john_doe", "email", "john@example.com")
        ));
        ConsumerRecord<String, String> record = new ConsumerRecord<>("user.all.response", 0, 0L, "key", json);
        consoleConsumer.receiveAllUsers(record);
        assertTrue(captureOutput().contains("john_doe"));
    }

    @Test
    void testReceiveAllUsers_emptyList() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(List.of());
        ConsumerRecord<String, String> record = new ConsumerRecord<>("user.all.response", 0, 0L, "key", json);
        consoleConsumer.receiveAllUsers(record);
        assertTrue(captureOutput().contains("No users found."));
    }

    @Test
    void testReceiveAllRecipes_validJson() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(List.of(Map.of("id", 1, "title", "Recipe1")));
        consoleConsumer.receiveAllRecipes(json);
        assertTrue(captureOutput().contains("Recipe1"));
    }

    @Test
    void testReceiveAllRecipes_emptyList() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(List.of());
        consoleConsumer.receiveAllRecipes(json);
        assertTrue(captureOutput().contains("No recipes found."));
    }

    @Test
    void testReceiveRecommendedRecipes_validJson() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(List.of(Map.of("id", 1, "title", "Best Recipe")));
        consoleConsumer.receiveRecommendedRecipes(json);
        assertTrue(captureOutput().contains("Best Recipe"));
    }

    @Test
    void testReceiveUserRecipes_validJson() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(List.of(Map.of("id", 1, "title", "User Recipe")));
        consoleConsumer.receiveUserRecipes(json);
        assertTrue(captureOutput().contains("User Recipe"));
    }

    @Test
    void testHandleSuccessMessages() {
        consoleConsumer.handleUserSuccess("User success");
        consoleConsumer.handleRecipeSuccess("Recipe success");
        assertTrue(captureOutput().contains("User success"));
        assertTrue(captureOutput().contains("Recipe success"));
    }
}
